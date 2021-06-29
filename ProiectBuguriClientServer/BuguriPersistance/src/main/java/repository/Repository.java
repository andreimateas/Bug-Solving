package repository;

public interface Repository<E> {

    Iterable<E> findAll();

    E save(E entity);

    E delete(Long id);
}
