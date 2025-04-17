import java.util.*;
import java.util.stream.Collectors;

public class QThreeEC {
    private Map<String, Integer> candidates;
    private PriorityQueue<CandidateVotes> maxHeap;
    private int totalVotes;
    private int p;

    public QThreeEC() {
        this.candidates = new HashMap<>();
        this.maxHeap = new PriorityQueue<>((a, b) -> {
            if (b.votes != a.votes) return b.votes - a.votes;
            return a.candidate.compareTo(b.candidate);
        });
        this.totalVotes = 0;
        this.p = 0;
    }

    public void initializeCandidates(List<String> candidatesList) {
        candidates.clear();
        maxHeap.clear();
        totalVotes = 0;

        for (String candidate : candidatesList) {
            candidates.put(candidate, 0);
            maxHeap.offer(new CandidateVotes(candidate, 0));
        }
    }

    public void setTotalVotes(int p) {
        this.p = p;
    }

    public boolean castVote(String candidate) {
        if (!candidates.containsKey(candidate)) return false;

        int newVotes = candidates.get(candidate) + 1;
        candidates.put(candidate, newVotes);
        totalVotes++;
        maxHeap.offer(new CandidateVotes(candidate, newVotes));
        return true;
    }

    public boolean castRandomVote() {
        if (candidates.isEmpty()) return false;

        List<String> candidateList = new ArrayList<>(candidates.keySet());
        String randomCandidate = candidateList.get(new Random().nextInt(candidateList.size()));
        return castVote(randomCandidate);
    }

    public boolean rigElection(String candidate) {
        if (!candidates.containsKey(candidate)) return false;

        for (String c : candidates.keySet()) {
            candidates.put(c, 0);
        }
        totalVotes = 0;

        int otherCount = candidates.size() - 1;
        int riggedVotes = Math.max(p - otherCount, 1);
        candidates.put(candidate, riggedVotes);
        totalVotes += riggedVotes;

        for (String c : candidates.keySet()) {
            if (!c.equals(candidate) && totalVotes < p) {
                candidates.put(c, 1);
                totalVotes++;
            }
        }

        maxHeap.clear();
        for (Map.Entry<String, Integer> entry : candidates.entrySet()) {
            maxHeap.offer(new CandidateVotes(entry.getKey(), entry.getValue()));
        }

        return true;
    }

    public List<String> getTopKCandidates(int k) {
        return candidates.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = b.getValue().compareTo(a.getValue());
                    return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
                })
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void auditElection() {
        candidates.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = b.getValue().compareTo(a.getValue());
                    return cmp != 0 ? cmp : a.getKey().compareTo(b.getKey());
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

    public static void main(String[] args) {
        List<String> candidatePool = Arrays.asList(
                "Marcus Fenix", "Dominic Santiago", "Damon Baird", "Cole Train", "Anya Stroud",
                "Victor Hoffman", "Clayton Carmine", "Queen Myrrah", "Jinn", "Paduk"
        );

        Random rand = new Random();

        for (int testNumber = 1; testNumber <= 5; testNumber++) {
            QThreeEC election = new QThreeEC();

            int numCandidates = rand.nextInt(5) + 3; // 3–7
            int p = rand.nextInt(11) + 5; // 5–15

            List<String> shuffled = new ArrayList<>(candidatePool);
            Collections.shuffle(shuffled);
            List<String> candidates = shuffled.subList(0, numCandidates);

            election.initializeCandidates(candidates);
            election.setTotalVotes(p);

            System.out.println("=== Randomized Election Test #" + testNumber + " ===");
            System.out.println("Candidates: " + candidates);
            System.out.println("Total votes (p): " + p);

            for (int i = 0; i < p; i++) {
                election.castRandomVote();
            }

            int k = Math.min(3, candidates.size());
            System.out.println("Top " + k + " candidates after voting: " + election.getTopKCandidates(k));

            String riggedCandidate = candidates.get(rand.nextInt(candidates.size()));
            election.rigElection(riggedCandidate);
            System.out.println("Election rigged for: " + riggedCandidate);
            System.out.println("Top " + k + " candidates after rigging: " + election.getTopKCandidates(k));

            System.out.println("auditElection():");
            election.auditElection();
            System.out.println("------\n");
        }
    }
}
