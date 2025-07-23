import os
import uuid
from flask import Flask, request, jsonify
from flask_cors import CORS
from dotenv import load_dotenv

# Import your custom modules
from text_extractor import get_text_from_file
import requests
from gemini import (
    extract_resume_data_with_llm,
    extract_jd_data_with_llm,
    analyze_match_with_llm,
)

# Load environment variables from .env file
load_dotenv()

# Initialize Flask app
app = Flask(__name__)

# Enable CORS to allow requests from your Next.js frontend
CORS(app) 

# Define a temporary directory to store uploaded files
TEMP_DIR = "temp_files"
if not os.path.exists(TEMP_DIR):
    os.makedirs(TEMP_DIR)

@app.route("/", methods=["GET"])
def index():
    return "Welcome to the IntelliMatch NLP API! Use the /api/analyze endpoint to analyze resumes and job descriptions."

@app.route("/api/health", methods=["GET"])
def health_check():
    """Health check endpoint to verify API setup."""
    gemini_key = os.getenv("GEMINI_API_KEY")
    return jsonify({
        "status": "healthy",
        "gemini_configured": bool(gemini_key and gemini_key != "your_gemini_api_key_here"),
        "temp_dir_exists": os.path.exists(TEMP_DIR)
    })

@app.route("/api/analyze", methods=["POST"])
def analyze_documents():
    """
    API endpoint to analyze a resume and job description.
    Expects two JSON fields in the request: 'resumeUrl' and 'jobDescriptionUrl'.
    These should be direct links to the PDF/DOCX files (e.g., on AWS S3).
    """

    data = request.get_json()
    if not data or "resumeUrl" not in data or "jobDescriptionUrl" not in data:
        return jsonify({"error": "Both 'resumeUrl' and 'jobDescriptionUrl' are required in the request body."}), 400

    resume_url = data["resumeUrl"]
    jd_url = data["jobDescriptionUrl"]

    # 1. Download files from URLs
    unique_id = str(uuid.uuid4())
    
    # Detect file extensions from URLs
    resume_ext = ".pdf" if resume_url.lower().endswith('.pdf') else ".docx" if resume_url.lower().endswith('.docx') else ""
    jd_ext = ".pdf" if jd_url.lower().endswith('.pdf') else ".docx" if jd_url.lower().endswith('.docx') else ""
    
    resume_filename = f"{unique_id}_resume{resume_ext}"
    jd_filename = f"{unique_id}_jd{jd_ext}"

    resume_path = os.path.join(TEMP_DIR, resume_filename)
    jd_path = os.path.join(TEMP_DIR, jd_filename)

    try:
        # Add headers to mimic a browser request and handle potential authentication
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        
        print(f"Downloading resume from: {resume_url}")
        resume_resp = requests.get(resume_url, timeout=30, headers=headers)
        print(f"Resume download status: {resume_resp.status_code}")
        
        print(f"Downloading job description from: {jd_url}")
        jd_resp = requests.get(jd_url, timeout=30, headers=headers)
        print(f"JD download status: {jd_resp.status_code}")
        
        if resume_resp.status_code != 200 or jd_resp.status_code != 200:
            return jsonify({"error": f"Failed to download files. Resume: {resume_resp.status_code}, JD: {jd_resp.status_code}"}), 400

        with open(resume_path, "wb") as f:
            f.write(resume_resp.content)
        with open(jd_path, "wb") as f:
            f.write(jd_resp.content)

        # 2. Process files and run analysis
        print(f"Processing resume file: {resume_path}")
        print(f"Processing job description file: {jd_path}")
        
        resume_text = get_text_from_file(resume_path)
        jd_text = get_text_from_file(jd_path)
        
        print(f"Resume text extracted: {len(resume_text) if resume_text else 0} characters")
        print(f"JD text extracted: {len(jd_text) if jd_text else 0} characters")

        if not resume_text or not jd_text:
            error_msg = []
            if not resume_text:
                error_msg.append("resume")
            if not jd_text:
                error_msg.append("job description")
            return jsonify({"error": f"Could not extract text from {' and '.join(error_msg)}. Ensure they are text-based PDF or DOCX files."}), 500

        resume_data = extract_resume_data_with_llm(resume_text)
        print(f"Resume data extracted: {bool(resume_data)}")
        
        jd_data = extract_jd_data_with_llm(jd_text)
        print(f"JD data extracted: {bool(jd_data)}")

        if not resume_data or not jd_data:
            error_msg = []
            if not resume_data:
                error_msg.append("resume")
            if not jd_data:
                error_msg.append("job description")
            return jsonify({"error": f"Failed to get structured data from LLM for {' and '.join(error_msg)}."}), 500

        print("Starting final analysis...")
        analysis_report = analyze_match_with_llm(resume_data, jd_data)
        print(f"Analysis report generated: {bool(analysis_report)}")

        if not analysis_report:
            return jsonify({"error": "Failed to generate the final analysis report."}), 500

        # 3. Return the final report
        return jsonify(analysis_report), 200

    except requests.exceptions.RequestException as e:
        print(f"Network error occurred: {e}")
        return jsonify({"error": f"Failed to download files: {str(e)}"}), 400
    except ImportError as e:
        print(f"Import error occurred: {e}")
        return jsonify({"error": f"Missing required dependencies: {str(e)}"}), 500
    except FileNotFoundError as e:
        print(f"File not found error: {e}")
        return jsonify({"error": f"File operation failed: {str(e)}"}), 500
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        print(f"Error type: {type(e).__name__}")
        import traceback
        traceback.print_exc()
        return jsonify({"error": f"An internal server error occurred: {str(e)}"}), 500

    finally:
        if os.path.exists(resume_path):
            os.remove(resume_path)
        if os.path.exists(jd_path):
            os.remove(jd_path)

if __name__ == "__main__":
    # Use Gunicorn for production, but this is fine for local development
    app.run(debug=True, port=5001)
