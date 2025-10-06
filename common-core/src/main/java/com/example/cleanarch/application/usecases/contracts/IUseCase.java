package com.example.cleanarch.common.application.usecases.contracts;

import com.example.cleanarch.common.application.dto.contracts.IUseCaseRequest;
import com.example.cleanarch.common.application.dto.contracts.IPresenter;

public interface IUseCase {
    void execute(IUseCaseRequest request, IPresenter presenter);
}