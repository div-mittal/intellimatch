import { RegisterData, LoginData, User, MatchHistoryItem, MatchDetail, UploadResponse } from "@/types";

const BASE_URL = "/api";

// Helper function for API calls
async function apiCall<T>(
  endpoint: string,
  options?: RequestInit
): Promise<T> {
  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    credentials: "include", // Include cookies for auth
    headers: {
      ...options?.headers,
    },
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: "An error occurred" }));
    throw new Error(error.message || `HTTP error! status: ${response.status}`);
  }

  return response.json();
}

// Auth APIs
export const authApi = {
  register: async (data: RegisterData) => {
    return apiCall("/user/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
  },

  login: async (data: LoginData) => {
    return apiCall("/user/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
  },

  logout: async () => {
    const response = await fetch(`${BASE_URL}/user/logout`, {
      method: "POST",
      credentials: "include",
    });

    if (!response.ok) {
      throw new Error("Logout failed");
    }

    // Backend returns plain text, not JSON
    return response.text();
  },

  getUser: async (): Promise<User> => {
    return apiCall<User>("/user/get");
  },
};

// Match APIs
export const matchApi = {
  upload: async (resume: File, jobDescription: File): Promise<UploadResponse> => {
    const formData = new FormData();
    formData.append("resume", resume);
    formData.append("jobDescription", jobDescription);

    return apiCall<UploadResponse>("/upload", {
      method: "POST",
      body: formData,
    });
  },

  getHistory: async (): Promise<MatchHistoryItem[]> => {
    return apiCall<MatchHistoryItem[]>("/user/history");
  },

  getMatchDetails: async (matchId: string): Promise<MatchDetail> => {
    return apiCall<MatchDetail>(`/user/match/${matchId}`);
  },
};
