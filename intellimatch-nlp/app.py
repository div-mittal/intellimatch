import os
import uuid
from flask import Flask, request, jsonify
from flask_cors import CORS
from dotenv import load_dotenv

# Import your custom modules
from text_extractor import get_text_from_file
from llm_processors import (
    extract_resume_data_with_llm,
    extract_jd_data_with_llm,
    analyze_match_with_llm,
)

# Load environment variables from .env file
load_dotenv()

# Initialize Flask app
app = Flask(__name__)

# Enable CORS to allow requests from your Next.js frontend
# In production, you should restrict the origin to your frontend's domain
CORS(app) 

# Define a temporary directory to store uploaded files
TEMP_DIR = "temp_files"
if not os.path.exists(TEMP_DIR):
    os.makedirs(TEMP_DIR)

@app.route("/api/analyze", methods=["POST"])
def analyze_documents():
    """
    API endpoint to analyze a resume and job description.
    Expects two file uploads in the request: 'resume' and 'jobDescription'.
    """
    # 1. Validate file uploads
    if "resume" not in request.files or "jobDescription" not in request.files:
        return jsonify({"error": "Both 'resume' and 'jobDescription' files are required."}), 400

    resume_file = request.files["resume"]
    jd_file = request.files["jobDescription"]

    if resume_file.filename == "" or jd_file.filename == "":
        return jsonify({"error": "Files must not be empty."}), 400

    # 2. Save files temporarily
    # Use a unique filename to avoid conflicts
    unique_id = str(uuid.uuid4())
    resume_filename = f"{unique_id}_resume_{resume_file.filename}"
    jd_filename = f"{unique_id}_jd_{jd_file.filename}"
    
    resume_path = os.path.join(TEMP_DIR, resume_filename)
    jd_path = os.path.join(TEMP_DIR, jd_filename)

    resume_file.save(resume_path)
    jd_file.save(jd_path)

    try:
        # 3. Process files and run analysis
        # Step A: Extract text from both files
        resume_text = get_text_from_file(resume_path)
        jd_text = get_text_from_file(jd_path)

        if not resume_text or not jd_text:
            return jsonify({"error": "Could not extract text from one or both files. Ensure they are text-based PDF or DOCX."}), 500

        # Step B: Get structured data from LLM
        resume_data = extract_resume_data_with_llm(resume_text)
        jd_data = extract_jd_data_with_llm(jd_text)

        if not resume_data or not jd_data:
            return jsonify({"error": "Failed to get structured data from LLM for one or both documents."}), 500

        # Step C: Get the final analysis from LLM
        analysis_report = analyze_match_with_llm(resume_data, jd_data)

        if not analysis_report:
            return jsonify({"error": "Failed to generate the final analysis report."}), 500

        # 4. Return the final report
        return jsonify(analysis_report), 200

    except Exception as e:
        # Catch any other unexpected errors
        print(f"An unexpected error occurred: {e}")
        return jsonify({"error": "An internal server error occurred."}), 500
    
    finally:
        # 5. Clean up temporary files
        if os.path.exists(resume_path):
            os.remove(resume_path)
        if os.path.exists(jd_path):
            os.remove(jd_path)

if __name__ == "__main__":
    # Use Gunicorn for production, but this is fine for local development
    app.run(debug=True, port=5001)
