'use client';

import React, { useState, useRef } from 'react';
import { uploadData } from '@/api/uploadData';
import { Footer } from '@/components/footer';

export default function UploadPage() {
  const [resume, setResume] = useState<File | null>(null);
  const [jobDescription, setJobDescription] = useState<File | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [uploading, setUploading] = useState<boolean>(false);

  const resumeRef = useRef<HTMLInputElement>(null);
  const jdRef = useRef<HTMLInputElement>(null);

  const validateFile = (file: File): string | null => {
    const allowedTypes = [
      'application/pdf',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    ];
    if (!allowedTypes.includes(file.type)) {
      return 'Only PDF or DOCX files are allowed.';
    }
    if (file.size > 5 * 1024 * 1024) {
      return 'File size must be less than 5MB.';
    }
    return null;
  };

  const handleFileChange = (
    event: React.ChangeEvent<HTMLInputElement>,
    setter: React.Dispatch<React.SetStateAction<File | null>>
  ) => {
    setError(null);
    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];
      const validationError = validateFile(file);
      if (validationError) {
        setError(validationError);
        setter(null);
      } else {
        setter(file);
      }
    }
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);

    if (!resume || !jobDescription) {
      setError('Please upload both files.');
      return;
    }

    setUploading(true);
    try {
      const response = await uploadData(resume, jobDescription);
      if (!response.ok) {
        const errorData = await response.json();
        setError(errorData.message || 'An error occurred during upload.');
        return;
      }
      const result = await response.json();
      console.log('Upload successful:', result);
      
      if (resumeRef.current) resumeRef.current.value = '';
      if (jdRef.current) jdRef.current.value = '';
    } catch (err) {
      setError('An error occurred during upload. Please try again.');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="min-h-screen p-6 bg-gray-100">
      <h1 className="text-2xl font-bold mb-4">Upload Resume & Job Description</h1>
      <p className="mb-6 text-gray-700">
        Please upload your resume and the job description you are applying for.
      </p>

      <form
        className="bg-white p-6 rounded-lg shadow-md max-w-xl mx-auto"
        onSubmit={handleSubmit}
        noValidate
      >
        {/* Resume Upload */}
        <div className="mb-4">
          <label htmlFor="resume" className="block text-sm font-medium text-gray-700 mb-2">
            Resume (PDF or DOCX)
          </label>
          <input
            ref={resumeRef}
            type="file"
            id="resume"
            accept=".pdf,.docx"
            className="block w-full border border-gray-300 rounded-lg p-2.5 text-sm text-gray-900 focus:ring-blue-500 focus:border-blue-500"
            onChange={(e) => handleFileChange(e, setResume)}
            disabled={uploading}
          />
          {resume && <p className="mt-1 text-sm text-gray-600">Selected: {resume.name}</p>}
        </div>

        {/* Job Description Upload */}
        <div className="mb-4">
          <label htmlFor="job-description" className="block text-sm font-medium text-gray-700 mb-2">
            Job Description (PDF or DOCX)
          </label>
          <input
            ref={jdRef}
            type="file"
            id="job-description"
            accept=".pdf,.docx"
            className="block w-full border border-gray-300 rounded-lg p-2.5 text-sm text-gray-900 focus:ring-blue-500 focus:border-blue-500"
            onChange={(e) => handleFileChange(e, setJobDescription)}
            disabled={uploading}
          />
          {jobDescription && (
            <p className="mt-1 text-sm text-gray-600">Selected: {jobDescription.name}</p>
          )}
        </div>

        {/* Error Display */}
        {error && (
          <div
            className="mb-4 text-red-600 text-sm"
            role="alert"
            aria-live="assertive"
          >
            {error}
          </div>
        )}

        {/* Submit Button */}
        <button
          type="submit"
          className="w-full bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
          disabled={uploading}
        >
          {uploading ? 'Uploading...' : 'Upload'}
        </button>

        <p className="mt-4 text-sm text-gray-500 text-center">
          By uploading, you agree to our{' '}
          <a href="/terms" className="text-blue-600 hover:underline">
            Terms of Service
          </a>{' '}
          and{' '}
          <a href="/privacy" className="text-blue-600 hover:underline">
            Privacy Policy
          </a>.
        </p>
      </form>
      <Footer />
    </div>
  );
}
