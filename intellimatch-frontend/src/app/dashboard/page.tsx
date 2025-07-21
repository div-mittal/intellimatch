'use client';

import React, { useState, useEffect } from 'react';
import { Footer } from '@/components/footer';
import Match from '@/types/Match';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from "@/components/ui/card"
import { fetchMatches } from '@/api/getMatchHistory';

export default function DashboardPage() {
    const [matches, setMatches] = useState<Match[]>([]);

    useEffect(() => {
        const loadMatches = async () => {
            try {
                const fetchedMatches = await fetchMatches();
                setMatches(fetchedMatches);
            } catch (error) {
                console.error('Error fetching matches:', error);
            }
        };

        loadMatches();
    }, []);

    const printMatches = () => {
        console.log(matches);
    };

    return (
        <>
            <h1 className="text-2xl font-bold mb-4">Dashboard</h1>
            <p className="mb-6 text-gray-700">
                Welcome to your dashboard! Here you can manage your uploads and view your application status.
            </p>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {matches.map((match) => (
                    <Card key={match.id} className="shadow-lg">
                        <CardContent>
                            <h2 className="text-lg font-semibold">{match.resumeName}</h2>
                            <p className="text-sm text-gray-600">Job Description: {match.jobDescriptionName}</p>
                            <p className="text-sm text-gray-600">Score: {match.score}</p>
                            <p className="text-sm text-gray-600">Match Date: {new Date(match.matchDate).toLocaleDateString()}</p>
                        </CardContent>
                        <div className="p-4">
                            <Button
                                variant="secondary"
                                size="sm"
                                className="w-full bg-blue-500 text-white"
                                onClick={() => window.location.href = `/match/${match.id}`}
                            >
                                View Details
                            </Button>
                        </div>
                    </Card>
                ))}
            </div>
            <Footer />
        </>
    )
}
