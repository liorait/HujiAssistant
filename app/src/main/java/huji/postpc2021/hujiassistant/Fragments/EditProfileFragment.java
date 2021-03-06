package huji.postpc2021.hujiassistant.Fragments;

import android.os.Bundle;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;
import huji.postpc2021.hujiassistant.StudentInfo;
import huji.postpc2021.hujiassistant.databinding.FragmentEditprofileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class EditProfileFragment extends Fragment {

    private TextView helloMessage;
    private EditText emailEditText, userNewPassword, userRepeatPassword, oldPassword;
    private ImageView btnEditProfile;
    private ImageView btnCancelEdit;
    private boolean isEdit = false;
    AutoCompleteTextView editDegreeTxt;
    AutoCompleteTextView editDegreeYearTxt;
    int PASSWORD_LENGTH = 8;
    FragmentEditprofileBinding binding;

    public EditProfileFragment(){
        super(R.layout.fragment_editprofile);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditprofileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocalDataBase dataBase = HujiAssistentApplication.getInstance().getDataBase();
        StudentInfo currentUser = dataBase.getCurrentUser();

        // find views
        helloMessage = view.findViewById(R.id.helloMessage);
        emailEditText = view.findViewById(R.id.usersEmailMyProfile);
        btnCancelEdit = view.findViewById(R.id.btnCancelEditProfile);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        userNewPassword = view.findViewById(R.id.userNewPassword1MyProfile);
        userRepeatPassword = view.findViewById(R.id.userNewPassword2MyProfile);
        oldPassword = view.findViewById(R.id.userOldPasswordMyProfile);
        emailEditText.setEnabled(false);
        editDegreeTxt = view.findViewById(R.id.autoCompleteTextViewDegreeEditInfo);
        editDegreeYearTxt = view.findViewById(R.id.autoCompleteTextViewYearEditInfo);

        // current student
        StudentInfo currentStudent = dataBase.getCurrentStudent();
        String degree = currentStudent.getDegree();
        String year = currentStudent.getYear();

        // binding
        String[] degreeArray = getResources().getStringArray(R.array.degreeTypesList);
        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdowndegreeitem, degreeArray);
        arrayAdapter.getFilter().filter("");
        binding.autoCompleteTextViewDegreeEditInfo.setAdapter(arrayAdapter);
        editDegreeTxt.setText(degree);

        // binding year
        String[] yearArray = getResources().getStringArray(R.array.yearArray);
        arrayAdapter.getFilter().filter("");
        binding.autoCompleteTextViewYearEditInfo.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownyearitem, yearArray));
        editDegreeYearTxt.setText(year);

        // set listener
        editDegreeTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), "???????? ?????????? ????????????", Toast.LENGTH_SHORT).show();
                dataBase.updateDegree(selection);
                arrayAdapter.getFilter().filter("");
                binding.autoCompleteTextViewDegreeEditInfo.setAdapter(arrayAdapter);
            }
        });

        editDegreeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayAdapter.getFilter().filter("");
                binding.autoCompleteTextViewDegreeEditInfo.setAdapter(arrayAdapter);
            }
        });

        editDegreeYearTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayAdapter.getFilter().filter("");
                binding.autoCompleteTextViewYearEditInfo.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownyearitem, yearArray));
            }
        });

        editDegreeYearTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), "???????? ?????????? ????????????", Toast.LENGTH_SHORT).show();
                dataBase.updateYear(selection);
                arrayAdapter.getFilter().filter("");
                binding.autoCompleteTextViewYearEditInfo.setAdapter(new ArrayAdapter(requireContext(), R.layout.dropdownyearitem, yearArray));
            }
        });

        // initialize screen appearance
        setViewsByState(false);
        setViewsContentByUser(currentUser);

        btnEditProfile.setOnClickListener(v -> {
            if (isEdit && checkValidation()) {
                btnCancelEdit.setVisibility(View.GONE);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(emailEditText.getText().toString(), oldPassword.getText().toString());

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(userNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), R.string.password_updated_Successfully_message, Toast.LENGTH_LONG).show();
                                                userNewPassword.setText("");
                                                userRepeatPassword.setText("");
                                                oldPassword.setText("");
                                            } else {
//                                                Log.d(TAG, "Error password not updated");
                                                Toast.makeText(getActivity(), R.string.password_failed_to_update_message, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
//                                    Log.d(TAG, "Error auth failed");
                                    Toast.makeText(getActivity(), R.string.incorrect_old_password, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
            }
            isEdit = !isEdit;
            setViewsByState(isEdit);
        });

        // cancel edit button callback
        btnCancelEdit.setOnClickListener(v -> cancelEditing());
    }

    /**
     * sets the views content as the given user fields
     */
    private void setViewsContentByUser(StudentInfo user) {
        String message = getResources().getString(R.string.hello) + " " + user.getPersonalName() + " " + user.getFamilyName();
        helloMessage.setText(message);
        emailEditText.setText(user.getEmail());
    }

    private void cancelEditing() {
        userNewPassword.setText("");
        userRepeatPassword.setText("");
        oldPassword.setText("");
        if (isEdit) {
            btnEditProfile.callOnClick();
        }
    }

    /**
     * sets the views state according to the given editState
     */
    private void setViewsByState(boolean isEditState) {
        if (isEditState) {
            btnCancelEdit.setVisibility(View.VISIBLE);
        } else {
            btnCancelEdit.setVisibility(View.GONE);
        }
        userNewPassword.setEnabled(isEditState);
        userRepeatPassword.setEnabled(isEditState);
        oldPassword.setEnabled(isEditState);
        int edit_ic = isEditState ? R.drawable.ic_save_profile : R.drawable.ic_edit_profile;
        btnEditProfile.setImageResource(edit_ic);
    }

    private boolean checkValidation(){
        String oldPass = oldPassword.getText().toString();
        String newPass = userNewPassword.getText().toString();
        String repeatPass = userRepeatPassword.getText().toString();
        if (oldPass.isEmpty()){
            Toast.makeText(getActivity(), R.string.old_password_is_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        if (newPass.isEmpty()){
            Toast.makeText(getActivity(), R.string.new_password_is_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        if (repeatPass.isEmpty()){
            Toast.makeText(getActivity(), R.string.repeat_password_is_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        if (!newPass.equals(repeatPass)){
            Toast.makeText(getActivity(), R.string.new_pass_is_not_equal_to_repeat_pass, Toast.LENGTH_LONG).show();
            return false;
        }
        if (newPass.length() < PASSWORD_LENGTH){
            Toast.makeText(getActivity(), R.string.please_enter_password_msg, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
