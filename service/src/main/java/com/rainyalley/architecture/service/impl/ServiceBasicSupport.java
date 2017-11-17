package com.rainyalley.architecture.service.impl;

import com.rainyalley.architecture.core.BeanMapConvertor;
import com.rainyalley.architecture.core.Page;
import com.rainyalley.architecture.dao.Dao;
import com.rainyalley.architecture.service.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.validation.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 实现了{@link Service}中所有方法的默认实现类
 * 该类可作为事务的通用实现
 *
 * @param <T>
 */
public abstract class ServiceBasicSupport<T> implements Service<T> {

    private Validator validator;

    @Override
    @Transactional
    public T save(T obj) {
        List<T> pojoList = this._get(obj, new Page());
        if (pojoList == null || pojoList.isEmpty()) {
            this.getDao().insert(obj);
        } else {
            this.getDao().update(obj);
        }
        return obj;
    }

    @Override
    @Transactional
    public int remove(T obj) {
        return this.getDao().delete(obj);
    }

    @Override
    @Transactional(readOnly = true)
    public T get(T obj) {
        List<T> pojoList = this._get(obj, new Page());
        if (pojoList == null || pojoList.isEmpty()) {
            return null;
        }
        return pojoList.get(0);
    }

    private List<T> _get(T obj, Page page) {
        Map<String, Object> params = BeanMapConvertor.merge(obj, page);
        return this.getDao().select(params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> get(T obj, Page page) {
        return this._get(obj, page);
    }


    protected abstract Dao<T> getDao();


    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validator = validatorFactory == null ? null : validatorFactory.getValidator();
    }

    protected final Validator validator() {
        if (this.validator == null) {
            this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        }
        return this.validator;
    }

    protected <T> void resolveConstraint(Set<ConstraintViolation<T>> result) {
        Assert.notNull(result);
        if (result.size() > 0) {
            StringBuilder message = new StringBuilder();
            Iterator<ConstraintViolation<T>> iterator = result.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> constraint = iterator.next();
                message.append(constraint.getPropertyPath()).append(" ");
                message.append(constraint.getMessage());
                if (iterator.hasNext()) {
                    message.append(", ");
                }
            }
            throw new ValidationException(message.toString());
        }
    }

    <T> void validate(T object, Class<?>... groups) {
        this.resolveConstraint(this.validator().validate(object, groups));
    }
}
