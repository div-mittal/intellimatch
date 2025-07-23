import pdfplumber
import docx

def extract_text_from_pdf(pdf_path):
    """Extracts all text from a PDF file."""
    try:
        all_text = ""
        with pdfplumber.open(pdf_path) as pdf:
            for page in pdf.pages:
                text = page.extract_text(x_tolerance=1)
                if text:
                    all_text += text + "\n"
        return all_text
    except Exception as e:
        print(f"Error extracting text from PDF: {e}")
        return None

def extract_text_from_docx(docx_path):
    """Extracts all text from a DOCX file."""
    try:
        doc = docx.Document(docx_path)
        all_text = "\n".join([para.text for para in doc.paragraphs])
        return all_text
    except Exception as e:
        print(f"Error extracting text from DOCX: {e}")
        return None

def get_text_from_file(file_path):
    """
    Detects file type and uses the appropriate text extractor.
    """
    if file_path.lower().endswith('.pdf'):
        return extract_text_from_pdf(file_path)
    elif file_path.lower().endswith('.docx'):
        return extract_text_from_docx(file_path)
    else:
        return None