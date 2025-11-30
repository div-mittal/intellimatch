export interface User {
  id: string;
  name: string;
  email: string;
  phoneNumber: string;
}

export interface MatchHistoryItem {
  id: string;
  resumeName: string;
  jobDescriptionName: string;
  resumeUrl: string;
  jobDescriptionUrl: string;
  matchDate: string;
  score: number;
  resultMessage: string;
  matchResult: MatchResultData | null;
}

export interface MatchResultData {
  id: string;
  atsScorePercent: number;
  summary: string;
  whatMatched: MatchedItem[];
  whatIsMissing: MissingItem[];
}

export interface MatchDetail {
  id: string;
  resumeName: string;
  jobDescriptionName: string;
  resumeUrl: string;
  jobDescriptionUrl: string;
  matchDate: string;
  score: number;
  resultMessage: string;
  matchResult: MatchResultData | null;
}

export interface MatchedItem {
  item: string;
  reason: string;
}

export interface MissingItem {
  item: string;
  recommendation: string;
}

export interface RegisterData {
  name: string;
  email: string;
  password: string;
  phoneNumber: string;
}

export interface LoginData {
  email: string;
  password: string;
}

export interface UploadResponse {
  id: string;
  userId: string;
  resumeName: string;
  jobDescriptionName: string;
  resumeUrl: string;
  jobDescriptionUrl: string;
  matchDate: string;
  matchResultId: string | null;
}
