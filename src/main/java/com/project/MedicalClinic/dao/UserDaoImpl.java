package com.project.MedicalClinic.dao;

import com.project.MedicalClinic.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    private EntityManager entityManager;

    @Autowired
    public UserDaoImpl(EntityManager theEntityManager) {
        this.entityManager = theEntityManager;
    }

    @Override
    public List<User> findAll() {
        TypedQuery<User> theQuery = entityManager.createQuery("from User", User.class);

        List<User> users = theQuery.getResultList();

        return users;
    }

    @Override
    public User findById(int theId) {

        User theUser = entityManager.find(User.class, theId);

        return theUser;
    }

    @Override
    @Transactional
    public User saveUser(User theUser) {

        User dbUser = entityManager.merge(theUser);

        return dbUser;
    }


    @Override
    public User findByUserName(String theUserName) {

        TypedQuery<User> theQuery = entityManager.createQuery("from User where userName=:uName and enabled=true", User.class);
        theQuery.setParameter("uName", theUserName);

        User theUser = null;
        try {
            theUser = theQuery.getSingleResult();
        } catch (Exception e) {
            theUser = null;
        }

        return theUser;
    }

    @Override
    @Transactional
    public User save(User theUser) {

        entityManager.merge(theUser);
        return theUser;
    }

    @Override
    @Transactional
    public void delete(User theUser) {
        entityManager.remove(theUser);
    }
    @Override
    @Transactional
    public void deleteById(int theId) {

        User userToDelete = entityManager.find(User.class, theId);
        if (userToDelete != null) {
            entityManager.remove(userToDelete);
        }
    }
}
