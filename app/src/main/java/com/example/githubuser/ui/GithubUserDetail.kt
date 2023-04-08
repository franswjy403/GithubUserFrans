package com.example.githubuser.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.githubuser.R
import com.example.githubuser.data.local.entity.User
import com.example.githubuser.data.repository.Result
import com.example.githubuser.databinding.FragmentGithubUserDetailBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class GithubUserDetail : Fragment(), View.OnClickListener {

    private var _binding: FragmentGithubUserDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var userGlobal: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGithubUserDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataUsername = GithubUserDetailArgs.fromBundle(arguments as Bundle).username
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: UsersViewModel by viewModels {
            factory
        }

        viewModel.getUserDetail(dataUsername).observe(viewLifecycleOwner) { result ->
            if(result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        setUserData(result.data)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            context,
                            "Terjadi kesalahan" + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        username = dataUsername

        val sectionsPagerAdapter = SectionsPagerAdapter(requireActivity())
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        binding.fabAdd.setOnClickListener(this)
    }

    private fun setUserData(user: User) {
        userGlobal = user
        Glide.with(requireActivity())
            .load(user.avatarUrl)
            .circleCrop()
            .into(binding.ivProfile)
        binding.tvUsername.text = user.username
        binding.tvName.text = user.name
        binding.tvFollowers.text = resources.getString(R.string.followers_count, user.followers)
        binding.tvFollowing.text = resources.getString(R.string.following_count, user.following)
        if(user.isFav){
            binding.fabAdd.setImageDrawable(ContextCompat.getDrawable(binding.fabAdd.context, R.drawable.ic_baseline_favorite_24))
        } else {
            binding.fabAdd.setImageDrawable(ContextCompat.getDrawable(binding.fabAdd.context, R.drawable.ic_baseline_favorite_border_24))
        }
    }

    override fun onClick(v: View?) {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: UsersViewModel by viewModels {
            factory
        }
        viewModel.changeFavorite(userGlobal)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.following
        )
        public lateinit var username: String
    }
}