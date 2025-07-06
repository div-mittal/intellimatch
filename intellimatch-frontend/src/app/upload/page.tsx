export default function UploadPage() {
  return (
    <div className="min-h-screen p-6 bg-gray-100">
      <h1 className="text-2xl font-bold mb-4">Upload Resume & Job Description</h1>
        <p className="mb-6">Please upload your resume and the job description you are applying for.</p>
        <form className="bg-white p-6 rounded shadow-md">
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-2" htmlFor="resume">
            Resume (PDF or DOCX)
          </label>
          <input
            type="file"
            id="resume"
            accept=".pdf,.docx"
            className="block w-full text-sm text-gray-900 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 p-2.5"
            required
          />
        </div>
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-2" htmlFor="job-description">
            Job Description (PDF or DOCX)
          </label>
          <input
            type="file"
            id="job-description"
            accept=".pdf,.docx"
            className="block w-full text-sm text-gray-900 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 p-2.5"
            required
          />
        </div>
      </form>
    </div>
  );
}