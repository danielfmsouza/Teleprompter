package com.easyapps.singerpro.presentation.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.easyapps.singerpro.application.LyricApplicationService;
import com.easyapps.singerpro.application.command.AddLyricCommand;
import com.easyapps.singerpro.application.command.UpdateLyricCommand;
import com.easyapps.singerpro.domain.model.lyric.Configuration;
import com.easyapps.singerpro.domain.model.lyric.IConfigurationRepository;
import com.easyapps.singerpro.domain.model.lyric.ILyricRepository;
import com.easyapps.singerpro.domain.model.lyric.Lyric;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidFileSystemLyricRepository;
import com.easyapps.singerpro.infrastructure.persistence.lyric.AndroidPreferenceConfigurationRepository;
import com.easyapps.singerpro.presentation.helper.ActivityUtils;
import com.easyapps.teleprompter.R;

/**
 * A simple {@link Fragment} subclass the holds the creation and edition of a Lyric
 */
public class MaintainLyricFragment extends Fragment {
    private static final String TEXT_WRITTEN = "TEXT_WRITTEN";
    private static final String SONG_NUMBER = "SONG_NUMBER";
    private static final String FILE_NAME = "FILE_NAME";

    private Operation mOperation = Operation.NEW_LYRIC;
    private Lyric mLyric;

    private LyricApplicationService mAppService;
    private OnSaveItemListener mListener;

    public MaintainLyricFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int songNumber = getArguments().getInt(SONG_NUMBER);
            mLyric = Lyric.newCompleteInstance(getArguments().getString(FILE_NAME),
                    getArguments().getString(TEXT_WRITTEN),
                    Configuration.newLightInstance(songNumber));
        }

        ILyricRepository mLyricRepository =
                new AndroidFileSystemLyricRepository(getActivity());
        IConfigurationRepository mConfigRepository =
                new AndroidPreferenceConfigurationRepository(getActivity());
        mAppService = new LyricApplicationService(mLyricRepository, null,
                mConfigRepository, null, null);
    }

    private void saveLyricFile() {
        String fileName = getFileNameContent();
        String songNumber = getSongNumberContent();

        if (fileName.trim().equals(""))
            setErrorFileNameRequired();
        if (songNumber.trim().equals(""))
            setErrorSongNumberRequired();
        if (!fileName.trim().equals("") && !songNumber.trim().equals("")) {
            try {
                if (mOperation == Operation.EDIT_LYRIC) {
                    UpdateLyricCommand cmd = new UpdateLyricCommand(fileName, getTextContent(),
                            songNumber, mLyric.getName());

                    mAppService.updateLyric(cmd);
                } else {
                    AddLyricCommand cmd = new AddLyricCommand(fileName, getTextContent(), songNumber);
                    mAppService.addLyric(cmd);
                }

                String message = getResources().getString(R.string.file_saved);
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                mListener.onSaveItem();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setSaveButtonOnClickListener(View v) {
        ImageButton btnSaveLyric = v.findViewById(R.id.btnSave);
        btnSaveLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLyricFile();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_maintain_lyric, container, false);

        EditText etLyricContent = v.findViewById(R.id.etTextFile);
        EditText etLyricName = v.findViewById(R.id.etFileName);
        EditText etSongNumber = v.findViewById(R.id.etSongNumber);
        etSongNumber.requestFocus();

        if (mLyric != null) {
            setTextFields(mLyric, etLyricContent, etLyricName, etSongNumber);
        } else {
            tryLoadLyricToUpdate(etLyricContent, etLyricName, etSongNumber);
        }
        setSaveButtonOnClickListener(v);

        return v;
    }

    private void setTextFields(Lyric lyric, EditText etLyricContent, EditText etLyricName, EditText etSongNumber) {
        etLyricContent.setText(lyric.getContent());
        etLyricName.setText(lyric.getName());
        etSongNumber.setText(String.valueOf(lyric.getConfiguration().getSongNumber()));
    }

    private void tryLoadLyricToUpdate(EditText etLyricContent, EditText etLyricName,
                                      EditText etSongNumber) {
        String lyricName = ActivityUtils.getFileNameParameter(getActivity().getIntent());
        if (lyricName != null) {
            mOperation = Operation.EDIT_LYRIC;
            try {
                mLyric = mAppService.loadLyricWithConfiguration(lyricName);
                setTextFields(mLyric, etLyricContent, etLyricName, etSongNumber);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachListener(context);
    }

    private void attachListener(Context context) {
        if (context instanceof OnSaveItemListener) {
            mListener = (OnSaveItemListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSaveItemListener");
        }
    }

    private void setErrorFileNameRequired() {
        if (getView() != null) {
            EditText etFileName = getView().findViewById(R.id.etFileName);
            String error = getResources().getString(R.string.file_name_required);

            etFileName.setError(error);
        }
    }

    private void setErrorSongNumberRequired() {
        if (getView() != null) {
            EditText etSongNumber = getView().findViewById(R.id.etSongNumber);
            String error = getResources().getString(R.string.song_number_required);

            etSongNumber.setError(error);
        }
    }

    private String getTextContent() {
        if (getView() != null) {
            EditText etTextFile = getView().findViewById(R.id.etTextFile);
            return etTextFile.getText().toString();
        }
        return "";
    }

    private String getFileNameContent() {
        if (getView() != null) {
            EditText etFileName = getView().findViewById(R.id.etFileName);
            return etFileName.getText().toString();
        }
        return "";
    }

    private String getSongNumberContent() {
        if (getView() != null) {
            EditText etSongNumber = getView().findViewById(R.id.etSongNumber);
            return etSongNumber.getText().toString();
        }
        return "";
    }

    /**
     * This method is here only to support older versions (before API 23).
     *
     * @param activity Activity to be attached to this fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        attachListener(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateContent(String lyricName) {
        if (getView() != null) {
            ActivityUtils.setLyricFileNameParameter(lyricName, getActivity().getIntent());

            EditText etLyricContent = getView().findViewById(R.id.etTextFile);
            EditText etLyricName = getView().findViewById(R.id.etFileName);
            EditText etSongNumber = getView().findViewById(R.id.etSongNumber);

            tryLoadLyricToUpdate(etLyricContent, etLyricName, etSongNumber);
        }
    }

    public void newContent() {
        if (getView() != null) {
            EditText etLyricContent = getView().findViewById(R.id.etTextFile);
            EditText etLyricName = getView().findViewById(R.id.etFileName);
            EditText etSongNumber = getView().findViewById(R.id.etSongNumber);

            etLyricContent.setText("");
            etLyricName.setText("");
            etSongNumber.setText("");

            mOperation = Operation.NEW_LYRIC;
        }
    }

    public interface OnSaveItemListener {
        void onSaveItem();
    }

    private enum Operation {
        NEW_LYRIC,
        EDIT_LYRIC
    }
}


