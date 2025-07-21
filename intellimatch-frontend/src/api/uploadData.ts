export async function uploadData(resume: File, jobDescription: File): Promise<any> {
  const formData = new FormData();
  formData.append('resume', resume);
  formData.append('jobDescription', jobDescription);

  try {
    const response = await fetch('/api/upload', {
      method: 'POST',
      body: formData,
      credentials: 'include',
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data?.message || data || `HTTP error! status: ${response.status}`);
    }

    return data;
  } catch (error) {
    console.error('Error uploading files:', error);
    throw error;
  }
}