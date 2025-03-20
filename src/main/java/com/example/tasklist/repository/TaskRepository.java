package com.example.tasklist.repository;

import com.example.tasklist.model.Task;
import com.example.tasklist.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    // ... existing code ...
    
    default Specification<Task> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null) {
                return null;
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }
    
    default Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }
} 