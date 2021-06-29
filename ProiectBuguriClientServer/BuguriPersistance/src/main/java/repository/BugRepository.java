package repository;

import domain.Bug;

public interface BugRepository extends Repository<Bug>{

    Iterable<Bug> getSolvedBugs();

    Iterable<Bug> getUnsolvedBugs();

    void modifyState(Long id,boolean newState,String newDescription);

    void modifyName(Long id,String newName);

}
