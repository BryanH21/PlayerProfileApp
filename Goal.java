//  abstract base for Roadmap goals

abstract class Goal {
    final String title;
    String status; // Examples include: "Not Started", "In Progress", "Done"
    Goal(String title, String status) {
        this.title = title;
        this.status = status;
    }

    /** Subclasses must compute progress 0..100 */
    abstract int progress();
    String summary() {
        return title + " (" + status + ")";
    }
}
class TrainingGoal extends Goal 
{
    final int targetSessions;
    int completedSessions;
    TrainingGoal(String title, String status, int targetSessions, int completedSessions) {
        super(title, status);
        this.targetSessions = targetSessions;
        this.completedSessions = completedSessions;
    }
    @Override int progress() {
        if (targetSessions <= 0) return 0;
        int p = Math.round((completedSessions * 100f) / targetSessions);
        return Math.max(0, Math.min(100, p));
    }
}
