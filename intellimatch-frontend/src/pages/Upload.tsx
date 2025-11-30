import { useState } from "react";
import { Layout } from "@/components/Layout";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { matchApi } from "@/lib/api";
import { useNavigate } from "react-router-dom";
import { useToast } from "@/hooks/use-toast";
import { Upload as UploadIcon, FileText, Loader2, CheckCircle2 } from "lucide-react";

export default function Upload() {
  const [resume, setResume] = useState<File | null>(null);
  const [jobDescription, setJobDescription] = useState<File | null>(null);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { toast } = useToast();

  const validateFile = (file: File, field: string) => {
    const validTypes = ["application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"];
    const maxSize = 5 * 1024 * 1024; // 5MB

    if (!validTypes.includes(file.type)) {
      return `${field} must be PDF or DOCX`;
    }
    if (file.size > maxSize) {
      return `${field} must be less than 5MB`;
    }
    return null;
  };

  const handleFileChange = (field: "resume" | "jobDescription", file: File | null) => {
    if (file) {
      const error = validateFile(file, field);
      if (error) {
        setErrors((prev) => ({ ...prev, [field]: error }));
        return;
      }
      setErrors((prev) => ({ ...prev, [field]: "" }));
    }

    if (field === "resume") {
      setResume(file);
    } else {
      setJobDescription(file);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validate both files
    const newErrors: Record<string, string> = {};
    if (!resume) newErrors.resume = "Resume is required";
    if (!jobDescription) newErrors.jobDescription = "Job description is required";

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setLoading(true);
    try {
      await matchApi.upload(resume!, jobDescription!);
      toast({
        title: "Match created successfully!",
        description: "Analysis is in progress. This may take a moment.",
      });
      navigate("/dashboard");
    } catch (error: any) {
      toast({
        title: "Upload failed",
        description: error.message || "Please try again.",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="container mx-auto px-4 py-8 max-w-2xl">
        <div className="space-y-2 mb-8">
          <h1 className="text-4xl font-bold">Upload Match</h1>
          <p className="text-muted-foreground text-lg">
            Upload your resume and job description for AI-powered ATS analysis
          </p>
        </div>

        <Card className="shadow-elegant">
          <CardHeader>
            <CardTitle>Upload Documents</CardTitle>
            <CardDescription>
              Both files must be PDF or DOCX format, max 5MB each
            </CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Resume Upload */}
              <div className="space-y-2">
                <Label htmlFor="resume">Resume</Label>
                <div
                  className={`border-2 border-dashed rounded-lg p-8 text-center transition-colors ${
                    resume
                      ? "border-success bg-success/5"
                      : errors.resume
                      ? "border-destructive bg-destructive/5"
                      : "border-border hover:border-primary"
                  }`}
                >
                  <input
                    id="resume"
                    type="file"
                    accept=".pdf,.docx"
                    onChange={(e) => handleFileChange("resume", e.target.files?.[0] || null)}
                    className="hidden"
                  />
                  <label htmlFor="resume" className="cursor-pointer">
                    <div className="flex flex-col items-center gap-3">
                      {resume ? (
                        <CheckCircle2 className="h-10 w-10 text-success" />
                      ) : (
                        <FileText className="h-10 w-10 text-muted-foreground" />
                      )}
                      <div>
                        <p className="font-medium">
                          {resume ? resume.name : "Click to upload resume"}
                        </p>
                        <p className="text-sm text-muted-foreground mt-1">
                          PDF or DOCX, max 5MB
                        </p>
                      </div>
                    </div>
                  </label>
                </div>
                {errors.resume && (
                  <p className="text-sm text-destructive">{errors.resume}</p>
                )}
              </div>

              {/* Job Description Upload */}
              <div className="space-y-2">
                <Label htmlFor="jobDescription">Job Description</Label>
                <div
                  className={`border-2 border-dashed rounded-lg p-8 text-center transition-colors ${
                    jobDescription
                      ? "border-success bg-success/5"
                      : errors.jobDescription
                      ? "border-destructive bg-destructive/5"
                      : "border-border hover:border-primary"
                  }`}
                >
                  <input
                    id="jobDescription"
                    type="file"
                    accept=".pdf,.docx"
                    onChange={(e) => handleFileChange("jobDescription", e.target.files?.[0] || null)}
                    className="hidden"
                  />
                  <label htmlFor="jobDescription" className="cursor-pointer">
                    <div className="flex flex-col items-center gap-3">
                      {jobDescription ? (
                        <CheckCircle2 className="h-10 w-10 text-success" />
                      ) : (
                        <FileText className="h-10 w-10 text-muted-foreground" />
                      )}
                      <div>
                        <p className="font-medium">
                          {jobDescription ? jobDescription.name : "Click to upload job description"}
                        </p>
                        <p className="text-sm text-muted-foreground mt-1">
                          PDF or DOCX, max 5MB
                        </p>
                      </div>
                    </div>
                  </label>
                </div>
                {errors.jobDescription && (
                  <p className="text-sm text-destructive">{errors.jobDescription}</p>
                )}
              </div>

              <Button
                type="submit"
                className="w-full gradient-primary"
                disabled={loading}
                size="lg"
              >
                {loading ? (
                  <>
                    <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                    Uploading & Analyzing...
                  </>
                ) : (
                  <>
                    <UploadIcon className="mr-2 h-5 w-5" />
                    Upload & Analyze
                  </>
                )}
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </Layout>
  );
}
