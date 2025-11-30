import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { ArrowRight, Calendar } from "lucide-react";
import { MatchHistoryItem } from "@/types";
import { Link } from "react-router-dom";
import { cn } from "@/lib/utils";

interface MatchCardProps {
  match: MatchHistoryItem;
}

export const MatchCard = ({ match }: MatchCardProps) => {
  const getScoreVariant = (score: number) => {
    if (score >= 75) return "success";
    if (score >= 50) return "warning";
    return "destructive";
  };

  const scoreVariant = getScoreVariant(match.score);

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }).format(date);
  };

  return (
    <Card className="hover:shadow-elegant transition-all duration-300 group">
      <CardContent className="p-6">
        <div className="flex items-start justify-between gap-4">
          <div className="flex-1 space-y-3">
            <div className="flex items-center gap-2 flex-wrap">
              <h3 className="font-semibold text-lg">
                {match.resumeName || `Match #${match.id.slice(0, 8)}`}
              </h3>
              <Badge
                variant={match.matchResult ? "outline" : "secondary"}
                className="capitalize"
              >
                {match.matchResult ? "completed" : "processing"}
              </Badge>
            </div>

            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Calendar className="h-4 w-4" />
              <span>{formatDate(match.matchDate)}</span>
            </div>

            <div className="flex items-center gap-3">
              <span className="text-sm font-medium text-muted-foreground">ATS Score:</span>
              <div className="flex items-center gap-2">
                <div className="h-2 w-32 bg-muted rounded-full overflow-hidden">
                  <div
                    className={cn(
                      "h-full transition-all duration-500",
                      scoreVariant === "success" && "bg-success",
                      scoreVariant === "warning" && "bg-warning",
                      scoreVariant === "destructive" && "bg-destructive"
                    )}
                    style={{ width: `${match.score}%` }}
                  />
                </div>
                <span className="font-bold text-sm">{match.score}%</span>
              </div>
            </div>
          </div>

          <Link to={`/match/${match.id}`}>
            <Button
              variant="ghost"
              size="sm"
              className="group-hover:bg-primary group-hover:text-primary-foreground transition-colors"
            >
              View
              <ArrowRight className="ml-2 h-4 w-4" />
            </Button>
          </Link>
        </div>
      </CardContent>
    </Card>
  );
};
