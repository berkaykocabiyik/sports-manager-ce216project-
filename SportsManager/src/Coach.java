package game;

public abstract class Coach {

    protected String id;
    protected String name;
    protected int experience;
    protected double coachingRating;
    protected String specialty;

    public Coach(String id, String name, int experience, double coachingRating, String specialty) {
        this.id = id;
        this.name = name;
        this.experience = experience;
        this.coachingRating = Math.max(1.0, Math.min(10.0, coachingRating));
        this.specialty = specialty;
    }

    public abstract Tactic selectTactic(Team team, Team opponent);

    public abstract Formation selectFormation(Team team);

    public abstract void setupLineup(Team team, Formation formation, Tactic tactic);

    public double getSuccessRating() {
        return (experience * 0.1 + coachingRating) / 2;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getExperience() {
        return experience;
    }

    public double getCoachingRating() {
        return coachingRating;
    }

    public String getSpecialty() {
        return specialty;
    }
}