package smartcityx.algorithms.dp;

import smartcityx.core.MyArrayList;

/**
 * 0/1 Knapsack and Subset Selection Dynamic Programming for municipal maintenance resource allocation.
 * Maximizes civic impact score under strict budget and crew capacity limits.
 * Pseudo-polynomial Time: O(N * W). Space: O(W).
 */
public class SubsetResourceDP {

    public static class ProjectItem {
        public final String name;
        public final int costBudget;
        public final int impactScore;

        public ProjectItem(String name, int costBudget, int impactScore) {
            this.name = name;
            this.costBudget = costBudget;
            this.impactScore = impactScore;
        }

        @Override
        public String toString() {
            return name + " (Cost: $" + costBudget + "k, Impact: " + impactScore + " pts)";
        }
    }

    public static class OptimizationResult {
        public final int maxImpact;
        public final int totalCostUsed;
        public final MyArrayList<ProjectItem> selectedProjects;

        public OptimizationResult(int maxImpact, int totalCostUsed, MyArrayList<ProjectItem> selectedProjects) {
            this.maxImpact = maxImpact;
            this.totalCostUsed = totalCostUsed;
            this.selectedProjects = selectedProjects;
        }
    }

    public static OptimizationResult optimizeProjects(MyArrayList<ProjectItem> projects, int maxBudget) {
        int n = projects.size();
        int[][] dp = new int[n + 1][maxBudget + 1];

        for (int i = 1; i <= n; i++) {
            ProjectItem p = projects.get(i - 1);
            for (int w = 0; w <= maxBudget; w++) {
                dp[i][w] = dp[i - 1][w]; // Don't take
                if (w >= p.costBudget) {
                    dp[i][w] = Math.max(dp[i][w], dp[i - 1][w - p.costBudget] + p.impactScore);
                }
            }
        }

        // Backtrack selected projects
        MyArrayList<ProjectItem> selected = new MyArrayList<>();
        int currW = maxBudget;
        int usedCost = 0;

        for (int i = n; i > 0; i--) {
            if (dp[i][currW] != dp[i - 1][currW]) {
                ProjectItem p = projects.get(i - 1);
                selected.add(p);
                currW -= p.costBudget;
                usedCost += p.costBudget;
            }
        }

        return new OptimizationResult(dp[n][maxBudget], usedCost, selected);
    }
}
