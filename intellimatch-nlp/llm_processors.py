import os
import json
import openai

def extract_resume_data_with_llm(text):
    """Analyzes resume text using an LLM and returns structured JSON."""
    client = openai.OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
    prompt = f"""
    You are an expert HR data extraction system. Analyze the following resume text and convert it into a structured JSON object.
    Follow this exact JSON structure:
    {{
      "contact_information": {{"name": "...", "email": "...", "phone": "...", "linkedin_url": "...", "github_url": "..."}},
      "summary": "...",
      "work_experience": [{{"job_title": "...", "company": "...", "start_date": "...", "end_date": "...", "responsibilities": ["..."]}}],
      "education": [{{"degree": "...", "institution": "...", "graduation_date": "..."}}],
      "skills": {{"technical": ["..."], "soft": ["..."]}},
      "projects": [{{"name": "...", "description": "...", "technologies": ["..."]}}]
    }}
    If a field is not present, omit it or set its value to null. Your entire output must be ONLY the JSON object.
    Resume Text:
    ---
    {text}
    ---
    """
    try:
        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.0,
            response_format={"type": "json_object"}
        )
        return json.loads(response.choices[0].message.content)
    except Exception as e:
        print(f"Error in LLM resume extraction: {e}")
        return None

def extract_jd_data_with_llm(text):
    """Analyzes job description text using an LLM and returns structured JSON."""
    client = openai.OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
    prompt = f"""
    You are an expert recruitment data analyst. Analyze the following job description and extract key skills, technologies, and qualifications into a structured JSON object.
    Use keys like "required_qualifications", "preferred_skills", "responsibilities". The values should be arrays of concise keywords.
    Your entire output must be ONLY a valid JSON object.
    Job Description Text:
    ---
    {text}
    ---
    """
    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.0,
            response_format={"type": "json_object"}
        )
        return json.loads(response.choices[0].message.content)
    except Exception as e:
        print(f"Error in LLM JD extraction: {e}")
        return None

def analyze_match_with_llm(resume_data, jd_data):
    """Compares resume and JD data using an LLM and returns a final analysis report."""
    client = openai.OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
    resume_json_str = json.dumps(resume_data, indent=2)
    jd_json_str = json.dumps(jd_data, indent=2)
    prompt = f"""
    You are an expert ATS and career coach. Analyze the provided resume and job description JSON.
    Generate a match report with an 'ats_score_percent' (0-100), a 'summary' (string), 'what_matched' (array of objects with 'item' and 'reason'), and 'what_is_missing' (array of objects with 'item' and 'recommendation').
    Your entire output must be ONLY a valid JSON object with that structure.

    --- RESUME JSON ---
    {resume_json_str}

    --- JOB DESCRIPTION JSON ---
    {jd_json_str}
    """
    try:
        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.1,
            response_format={"type": "json_object"}
        )
        return json.loads(response.choices[0].message.content)
    except Exception as e:
        print(f"Error in LLM match analysis: {e}")
        return None