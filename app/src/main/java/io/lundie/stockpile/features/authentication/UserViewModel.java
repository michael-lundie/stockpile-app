package io.lundie.stockpile.features.authentication;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.lundie.stockpile.data.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository;

    @Inject
    UserViewModel(UserRepository userRepository) { this.userRepository = userRepository; }


}
