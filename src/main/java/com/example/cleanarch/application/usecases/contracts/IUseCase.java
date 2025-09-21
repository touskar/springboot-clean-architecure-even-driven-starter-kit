package com.example.cleanarch.application.usecases.contracts;

import com.example.cleanarch.application.dto.contracts.IUseCaseRequest;
import com.example.cleanarch.application.dto.contracts.IPresenter;

public interface IUseCase {
    void execute(IUseCaseRequest request, IPresenter presenter);
}