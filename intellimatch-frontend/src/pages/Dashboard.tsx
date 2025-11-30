import { useEffect, useState } from "react";
import { Layout } from "@/components/Layout";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { MatchCard } from "@/components/MatchCard";
import { authApi, matchApi } from "@/lib/api";
import { User, MatchHistoryItem } from "@/types";
import { useNavigate, Link } from "react-router-dom";
import { useToast } from "@/hooks/use-toast";
import { Skeleton } from "@/components/ui/skeleton";
import { Upload, TrendingUp, FileCheck, AlertCircle } from "lucide-react";

export default function Dashboard() {
  const [user, setUser] = useState<User | null>(null);
  const [matches, setMatches] = useState<MatchHistoryItem[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const { toast } = useToast();

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const [userData, matchHistory] = await Promise.all([
        authApi.getUser(),
        matchApi.getHistory(),
      ]);
      setUser(userData);
      setMatches(matchHistory);
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
          description: "Failed to load dashboard data.",
          variant: "destructive",
        });
      }
    } finally {
      setLoading(false);
    }
  };

  const completedMatches = matches.filter((m) => m.status === "completed");
  const avgScore =
    completedMatches.length > 0
      ? Math.round(
          completedMatches.reduce((sum, m) => sum + m.atsScore, 0) / completedMatches.length
        )
      : 0;

  if (loading) {
    return (
      <Layout>
        <div className="container mx-auto px-4 py-8 space-y-8">
          <Skeleton className="h-12 w-64" />
          <div className="grid gap-6 md:grid-cols-3">
            <Skeleton className="h-32" />
            <Skeleton className="h-32" />
            <Skeleton className="h-32" />
          </div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="container mx-auto px-4 py-8 space-y-8">
        {/* Welcome Section */}
        <div className="space-y-2">
          <h1 className="text-4xl font-bold">Welcome back, {user?.name}! 👋</h1>
          <p className="text-muted-foreground text-lg">
            Track your resume matches and optimize for ATS success
          </p>
        </div>

        {/* Stats Cards */}
        <div className="grid gap-6 md:grid-cols-3">
          <Card className="shadow-card">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                Total Matches
              </CardTitle>
              <FileCheck className="h-5 w-5 text-primary" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold">{matches.length}</div>
              <p className="text-xs text-muted-foreground mt-1">
                {completedMatches.length} completed
              </p>
            </CardContent>
          </Card>

          <Card className="shadow-card">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                Average ATS Score
              </CardTitle>
              <TrendingUp className="h-5 w-5 text-success" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold">{avgScore}%</div>
              <p className="text-xs text-muted-foreground mt-1">
                Across all matches
              </p>
            </CardContent>
          </Card>

          <Card className="shadow-card gradient-primary text-primary-foreground">
            <CardHeader>
              <CardTitle className="text-sm font-medium opacity-90">
                Ready to improve?
              </CardTitle>
            </CardHeader>
            <CardContent>
              <Link to="/upload">
                <Button variant="secondary" className="w-full">
                  <Upload className="mr-2 h-4 w-4" />
                  New Match
                </Button>
              </Link>
            </CardContent>
          </Card>
        </div>

        {/* Recent Matches */}
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-bold">Recent Matches</h2>
            <Link to="/history">
              <Button variant="outline">View All</Button>
            </Link>
          </div>

          {matches.length === 0 ? (
            <Card className="shadow-card">
              <CardContent className="flex flex-col items-center justify-center py-12 text-center">
                <AlertCircle className="h-12 w-12 text-muted-foreground mb-4" />
                <h3 className="text-xl font-semibold mb-2">No matches yet</h3>
                <p className="text-muted-foreground mb-6">
                  Upload your first resume and job description to get started!
                </p>
                <Link to="/upload">
                  <Button className="gradient-primary">
                    <Upload className="mr-2 h-4 w-4" />
                    Create First Match
                  </Button>
                </Link>
              </CardContent>
            </Card>
          ) : (
            <div className="space-y-4">
              {matches.slice(0, 5).map((match) => (
                <MatchCard key={match.matchId} match={match} />
              ))}
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}
