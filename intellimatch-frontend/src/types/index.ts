export interface User {
  id: string;
  name: string;
  email: string;
  phoneNumber: string;
}

export interface MatchHistoryItem {
  matchId: string;
  uploadDateTime: string;
  resumeTitle?: string;
  jobTitle?: string;
  atsScore: number;
  status: "completed" | "processing" | "failed";
}

export interface MatchDetail {
  matchId: string;
  ats_score_percent: number;
  summary: string;
  what_matched: MatchedItem[];
  what_is_missing: MissingItem[];
  uploadDateTime: string;
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
