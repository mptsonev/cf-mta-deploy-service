package com.sap.cloud.lm.sl.cf.core.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sap.cloud.lm.sl.cf.core.dao.filters.OperationFilter;
import com.sap.cloud.lm.sl.cf.core.dto.persistence.OperationDto;
import com.sap.cloud.lm.sl.cf.core.helpers.OperationFactory;
import com.sap.cloud.lm.sl.cf.web.api.model.Operation;
import com.sap.cloud.lm.sl.common.ConflictException;
import com.sap.cloud.lm.sl.common.NotFoundException;

@Component
public class OperationDao {

    @Inject
    OperationDtoDao dao;
    @Inject
    OperationFactory operationFactory;

    public void add(Operation operation) throws ConflictException {
        OperationDto dto = operationFactory.toPersistenceDto(operation);
        dao.add(dto);
    }

    public void remove(String processId) throws NotFoundException {
        dao.remove(processId);
    }

    public void removeAll(List<String> processIds) throws NotFoundException {
        for (String processId : processIds) {
            dao.remove(processId);
        }
    }

    public Operation find(String processId) {
        OperationDto dto = dao.find(processId);
        if (dto == null) {
            return null;
        }
        return operationFactory.fromPersistenceDto(dto);
    }

    public Operation findRequired(String processId) throws NotFoundException {
        OperationDto dto = dao.findRequired(processId);
        return operationFactory.fromPersistenceDto(dto);
    }

    public List<Operation> find(OperationFilter filter) {
        List<OperationDto> dtos = dao.find(filter);
        return toOperations(dtos);
    }

    public List<Operation> findAll() {
        List<OperationDto> dtos = dao.findAll();
        return toOperations(dtos);
    }

    public void merge(Operation operation) throws NotFoundException {
        OperationDto dto = operationFactory.toPersistenceDto(operation);
        dao.merge(dto);
    }

    private List<Operation> toOperations(List<OperationDto> dtos) {
        return dtos.stream().map(dto -> operationFactory.fromPersistenceDto(dto)).collect(Collectors.toList());
    }

}
