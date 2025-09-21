package com.example.cleanarch.application.dto.contracts;

public interface IPresenter<T> {
    void present(IUseCaseResponse<T> response);
    ViewModel getViewModel();
}