package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.coroutineScope
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.viewmodel.AuthViewModel

class AuthFragment: Fragment() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthBinding.inflate(
            inflater,
            container,
            false
        )
        binding.buttonLogin.setOnClickListener {
            //считываем логин и пароль из полей
            val login = binding.login.text
            val pass = binding.password.text
            viewModel.getToken(login.toString(), pass.toString())
            viewModel.data.observe(viewLifecycleOwner) {
                if (it.token != null) {
                    findNavController().navigate(R.id.action_authFragment_to_feedFragment)
                }

            }

        }
        return binding.root
    }
}