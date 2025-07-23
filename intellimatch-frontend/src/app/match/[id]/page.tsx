'use client';

import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import Match from "@/types/Match";

export default function MatchPage() {
    const { id } = useParams();
    const [matchDetails, setMatchDetails] = useState<Match | null>(null);

    useEffect(() => {
        const fetchMatchDetails = async () => {
            const response = await fetch(`/api/user/match/${id}`);
            const data = await response.json();
            setMatchDetails(data);
        };

        fetchMatchDetails();
    }, [id]);

    return (
        <div>
            <h1>Match Details of {id}</h1>
        </div>
    );
}
