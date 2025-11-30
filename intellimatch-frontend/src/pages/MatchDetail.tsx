import { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { Layout } from "@/components/Layout";
import { ScoreBadge } from "@/components/ScoreBadge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { matchApi } from "@/lib/api";
import { MatchDetail as MatchDetailType } from "@/types";
import { useToast } from "@/hooks/use-toast";
import { Skeleton } from "@/components/ui/skeleton";
import { ArrowLeft, CheckCircle2, AlertTriangle, Calendar } from "lucide-react";
import { Badge } from "@/components/ui/badge";

export default function MatchDetail() {
  const { matchId } = useParams<{ matchId: string }>();
  const [match, setMatch] = useState<MatchDetailType | null>(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const { toast } = useToast();

  useEffect(() => {
    if (matchId) {
      loadMatchDetail(matchId);
    }
  }, [matchId]);

  const loadMatchDetail = async (id: string) => {
    try {
      const detail = await matchApi.getMatchDetails(id);
      setMatch(detail);
    } catch (error: any) {
      if (error.message.includes("401") || error.message.includes("Unauthorized")) {
        toast({
          title: "Session expired",
          description: "Please login again.",
          variant: "destructive",
        });
        navigate("/login");
      } else {
        toast({
          title: "Error",
          description: "Failed to load match details.",
          variant: "destructive",
        });
        navigate("/history");
      }
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat("en-US", {
      month: "long",
      day: "numeric",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }).format(date);
  };

  if (loading) {
    return (
      <Layout>
        <div className="container mx-auto px-4 py-8 space-y-8">
          <Skeleton className="h-12 w-64" />
          <div className="grid gap-8 lg:grid-cols-3">
            <Skeleton className="h-64 lg:col-span-1" />
            <Skeleton className="h-64 lg:col-span-2" />
          </div>
        </div>
      </Layout>
    );
  }

  if (!match) return null;

  return (
    <Layout>
      <div className="container mx-auto px-4 py-8 space-y-8">
        {/* Header */}
        <div className="space-y-4">
          <Link to="/history">
            <Button variant="ghost" className="gap-2">
              <ArrowLeft className="h-4 w-4" />
              Back to History
            </Button>
          </Link>

          <div className="flex items-start justify-between flex-wrap gap-4">
            <div className="space-y-2">
              <h1 className="text-4xl font-bold">Match Analysis</h1>
              <div className="flex items-center gap-2 text-muted-foreground">
                <Calendar className="h-4 w-4" />
                <span>{formatDate(match.uploadDateTime)}</span>
              </div>
            </div>
          </div>
        </div>

        {/* Main Content Grid */}
        <div className="grid gap-8 lg:grid-cols-3">
          {/* Score Card */}
          <Card className="shadow-elegant lg:col-span-1">
            <CardContent className="flex flex-col items-center justify-center py-12">
              <ScoreBadge score={match.ats_score_percent} size="lg" />
              <div className="mt-6 text-center">
                <p className="text-sm text-muted-foreground mb-2">Overall Match Quality</p>
                <Badge
                  variant={
                    match.ats_score_percent >= 75
                      ? "default"
                      : match.ats_score_percent >= 50
                      ? "secondary"
                      : "destructive"
                  }
                  className="text-sm"
                >
                  {match.ats_score_percent >= 75
                    ? "Excellent Match"
                    : match.ats_score_percent >= 50
                    ? "Good Match"
                    : "Needs Improvement"}
                </Badge>
              </div>
            </CardContent>
          </Card>

          {/* Summary Card */}
          <Card className="shadow-card lg:col-span-2">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                Summary
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-foreground leading-relaxed">{match.summary}</p>
            </CardContent>
          </Card>
        </div>

        {/* What Matched */}
        <Card className="shadow-card">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-success">
              <CheckCircle2 className="h-5 w-5" />
              What Matched
            </CardTitle>
          </CardHeader>
          <CardContent>
            {match.what_matched.length === 0 ? (
              <p className="text-muted-foreground">No specific matches identified.</p>
            ) : (
              <div className="space-y-4">
                {match.what_matched.map((item, index) => (
                  <div
                    key={index}
                    className="flex gap-4 p-4 bg-success/5 border border-success/20 rounded-lg"
                  >
                    <CheckCircle2 className="h-5 w-5 text-success flex-shrink-0 mt-0.5" />
                    <div className="flex-1">
                      <h4 className="font-medium mb-1">{item.item}</h4>
                      <p className="text-sm text-muted-foreground">{item.reason}</p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        {/* What is Missing */}
        <Card className="shadow-card">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-warning">
              <AlertTriangle className="h-5 w-5" />
              What is Missing
            </CardTitle>
          </CardHeader>
          <CardContent>
            {match.what_is_missing.length === 0 ? (
              <p className="text-muted-foreground">
                Great! Your resume covers all key requirements.
              </p>
            ) : (
              <div className="space-y-4">
                {match.what_is_missing.map((item, index) => (
                  <div
                    key={index}
                    className="flex gap-4 p-4 bg-warning/5 border border-warning/20 rounded-lg"
                  >
                    <AlertTriangle className="h-5 w-5 text-warning flex-shrink-0 mt-0.5" />
                    <div className="flex-1">
                      <h4 className="font-medium mb-1">{item.item}</h4>
                      <p className="text-sm text-muted-foreground">
                        <span className="font-medium">Recommendation:</span> {item.recommendation}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </Layout>
  );
}
