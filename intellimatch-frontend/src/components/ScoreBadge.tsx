import { cn } from "@/lib/utils";

interface ScoreBadgeProps {
  score: number;
  size?: "sm" | "md" | "lg";
  showLabel?: boolean;
}

export const ScoreBadge = ({ score, size = "md", showLabel = true }: ScoreBadgeProps) => {
  const getScoreColor = (score: number) => {
    if (score >= 75) return "success";
    if (score >= 50) return "warning";
    return "destructive";
  };

  const color = getScoreColor(score);

  const sizeClasses = {
    sm: "h-16 w-16 text-lg",
    md: "h-24 w-24 text-2xl",
    lg: "h-32 w-32 text-4xl",
  };

  const circumference = 2 * Math.PI * 45; // radius = 45
  const strokeDashoffset = circumference - (score / 100) * circumference;

  return (
    <div className="flex flex-col items-center gap-2">
      <div className={cn("relative", sizeClasses[size])}>
        <svg className="transform -rotate-90" width="100%" height="100%" viewBox="0 0 100 100">
          {/* Background circle */}
          <circle
            cx="50"
            cy="50"
            r="45"
            fill="none"
            stroke="currentColor"
            strokeWidth="8"
            className="text-muted"
            opacity="0.2"
          />
          {/* Progress circle */}
          <circle
            cx="50"
            cy="50"
            r="45"
            fill="none"
            stroke="currentColor"
            strokeWidth="8"
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            strokeLinecap="round"
            className={cn(
              "transition-all duration-1000 ease-out",
              color === "success" && "text-success",
              color === "warning" && "text-warning",
              color === "destructive" && "text-destructive"
            )}
          />
        </svg>
        <div className="absolute inset-0 flex items-center justify-center">
          <span className="font-bold">{score}%</span>
        </div>
      </div>
      {showLabel && (
        <span className="text-sm font-medium text-muted-foreground">
          ATS Score
        </span>
      )}
    </div>
  );
};
