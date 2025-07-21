import Match from '../types/Match';

export async function fetchMatches(): Promise<Match[]> {
    try {
        const response = await fetch('/api/user/history');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        return data as Match[];
    } catch (error) {
        console.error('Failed to fetch matches:', error);
        throw error;
    }
}