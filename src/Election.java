
import java.util.*;
import java.util.stream.Collectors;

public class Election {
    private Map<String, Integer> candidates;
    private PriorityQueue<CandidateVotes> maxHeap;
    private int totalVotes;

    public Election() {
        this.candidates = new HashMap<>();
        this.maxHeap = new PriorityQueue<>((a, b) -> b.votes - a.votes);
        this.totalVotes = 0;
    }

    public void initializeCandidates(List<String> candidates) {
        this.candidates.clear();
        this.maxHeap.clear();
        this.totalVotes = 0;

        for (String candidate : candidates) {
            this.candidates.put(candidate, 0);
            this.maxHeap.offer(new CandidateVotes(candidate, 0));
        }
    }

    public boolean castVote(String candidate) {
        if (!candidates.containsKey(candidate)) {
            return false;
        }

        int newVotes = candidates.get(candidate) + 1;
        candidates.put(candidate, newVotes);
        totalVotes++;
        maxHeap.offer(new CandidateVotes(candidate, newVotes));
        return true;
    }

    public boolean castRandomVote() {
        if (candidates.isEmpty()) {
            return false;
        }

        List<String> candidateList = new ArrayList<>(candidates.keySet());
        String randomCandidate = candidateList.get(new Random().nextInt(candidateList.size()));
        return castVote(randomCandidate);
    }

    public boolean rigElection(String candidate) {
        if (!candidates.containsKey(candidate)) {
            return false;
        }

        int currentVotes = candidates.get(candidate);
        int votesNeeded = totalVotes - currentVotes + 1;

        candidates.put(candidate, currentVotes + votesNeeded);
        totalVotes += votesNeeded;
        maxHeap.offer(new CandidateVotes(candidate, currentVotes + votesNeeded));
        return true;
    }

    public List<String> getTopKCandidates(int k) {
        return candidates.entrySet().stream()
                .sorted((a, b) -> {
                    int voteCompare = b.getValue().compareTo(a.getValue());
                    if (voteCompare != 0) return voteCompare;
                    return a.getKey().compareTo(b.getKey());
                })
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void auditElection() {
        candidates.entrySet().stream()
                .sorted((a, b) -> {
                    int voteCompare = b.getValue().compareTo(a.getValue());
                    if (voteCompare != 0) return voteCompare;
                    return a.getKey().compareTo(b.getKey());
                })
                .forEach(entry -> System.out.println(entry.getKey() + " - " + entry.getValue()));
    }

    private static class CandidateVotes {
        String candidate;
        int votes;

        public CandidateVotes(String candidate, int votes) {
            this.candidate = candidate;
            this.votes = votes;
        }
    }
}

public class ElectionSystem {
    private Election election;

    public ElectionSystem() {
        this.election = new Election();
    }

    public void runSampleElection() {
        List<String> candidates = Arrays.asList(
                "Marcus Fenix", "Dominic Santiago", "Damon Baird", "Cole Train", "Anya Stroud"
        );
        election.initializeCandidates(candidates);

        System.out.println("Sample operations:");
        election.castVote("Cole Train");
        election.castVote("Cole Train");
        election.castVote("Marcus Fenix");
        election.castVote("Anya Stroud");
        election.castVote("Anya Stroud");

        System.out.println("Top 3 candidates after 5 votes: " + election.getTopKCandidates(3));

        election.rigElection("Marcus Fenix");
        System.out.println("Top 3 candidates after rigging the election: " + election.getTopKCandidates(3));

        System.out.println("auditElection():");
        election.auditElection();
    }

    public static void main(String[] args) {
        ElectionSystem system = new ElectionSystem();
        system.runSampleElection();
    }
}
