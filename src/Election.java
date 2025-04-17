/*
import java.util.*;

class Election {
    private int totalVotes;
    private int currentVotes;
    private Map<String, Integer> voteCount;
    private PriorityQueue<String> maxHeap;

    public Election(int totalVotes) {
        this.totalVotes = totalVotes;
        this.currentVotes = 0;
        this.voteCount = new HashMap<>();
        this.maxHeap = new PriorityQueue<>((a, b) -> voteCount.get(b) - voteCount.get(a));
    }

    public void initializeCandidates(LinkedList<String> candidates) {
        for (String candidate : candidates) {
            voteCount.put(candidate, 0);
            maxHeap.offer(candidate);
        }
    }

    public void castVote(String candidate) {
        if (!voteCount.containsKey(candidate) || currentVotes >= totalVotes) return;
        voteCount.put(candidate, voteCount.get(candidate) + 1);
        currentVotes++;
        rebuildHeap();
    }

    public void castRandomVote() {
        if (currentVotes >= totalVotes) return;
        List<String> candidates = new ArrayList<>(voteCount.keySet());
        String randomCandidate = candidates.get(new Random().nextInt(candidates.size()));
        castVote(randomCandidate);
    }

    public void rigElection(String candidate) {
        if (!voteCount.containsKey(candidate)) return;

        int remainingVotes = totalVotes - currentVotes;
        if (remainingVotes <= 0) return;

        int maxOtherVotes = 0;
        for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
            if (!entry.getKey().equals(candidate)) {
                maxOtherVotes = Math.max(maxOtherVotes, entry.getValue());
            }
        }

        int current = voteCount.get(candidate);
        int neededToWin = maxOtherVotes - current + 1;

        // Don't exceed remaining votes
        int votesToAdd = Math.min(neededToWin, remainingVotes);
        voteCount.put(candidate, current + votesToAdd);
        currentVotes += votesToAdd;

        rebuildHeap();
    }



    public List<String> getTopKCandidates(int k) {
        List<String> result = new ArrayList<>();
        PriorityQueue<String> tempHeap = new PriorityQueue<>(
                (a, b) -> voteCount.get(b) - voteCount.get(a)
        );
        for (String candidate : voteCount.keySet()) {
            if (voteCount.get(candidate) > 0) {
                tempHeap.offer(candidate);
            }
        }
        while (k-- > 0 && !tempHeap.isEmpty()) {
            result.add(tempHeap.poll());
        }
        return result;
    }

    public void auditElection() {
        List<String> sorted = new ArrayList<>(voteCount.keySet());
        sorted.sort((a, b) -> voteCount.get(b) - voteCount.get(a));
        for (String candidate : sorted) {
            System.out.println(candidate + " - " + voteCount.get(candidate));
        }
    }

    private void rebuildHeap() {
        maxHeap.clear();
        maxHeap.addAll(voteCount.keySet());
    }
}

class ElectionSystem {
    public static void main(String[] args) {
        LinkedList<String> candidates = new LinkedList<>(Arrays.asList(
                "Marcus Fenix", "Dominic Santiago", "Damon Baird", "Cole Train", "Anya Stroud"
        ));

        int p = 5;
        Election election = new Election(p);
        election.initializeCandidates(candidates);

        election.castVote("Cole Train");
        election.castVote("Cole Train");
        election.castVote("Marcus Fenix");
        election.castVote("Anya Stroud");
        election.castVote("Anya Stroud");

        System.out.println("Top 3 candidates after 5 votes: " + election.getTopKCandidates(3));

        election.rigElection("Marcus Fenix");
        System.out.println("Top 3 candidates after rigging the election: " + election.getTopKCandidates(3));

        election.auditElection();
    }
}
*/

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
