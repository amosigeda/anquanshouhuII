package vip.inteltech.gat.chatutil;

import java.io.File;

import android.media.MediaRecorder;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.utils.AppContext;
import vip.inteltech.gat.utils.CommUtil;

public class AudioRecorder implements RecordImp {

	private MediaRecorder recorder;
	private String fileName;
	private String fileFolder =  AppContext.getInstance().getContext().getFilesDir().getAbsolutePath() + "/TestRecord";
			/*Environment.getExternalStorageDirectory()
			.getPath() + "/TestRecord";*/

	private boolean isRecording = false;

	@Override
	public void ready() {
		File file = new File(fileFolder);
		if (!file.exists()) {
			file.mkdir();
		}
		//fileName = getCurrentDate();

        try {
            fileName = "SendVoice";
            recorder = new MediaRecorder();
            recorder.setOutputFile(fileFolder + "/" + fileName + ".amr");
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置MediaRecorder的音频源为麦克风
            recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);// 设置MediaRecorder录制的音频格式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置MediaRecorder录制音频的编码为amr
        } catch (Exception e) {
            CommUtil.showMsgShort(R.string.open_mic_failed);
        }
    }
	public String getVoiceName(){
		return fileName + ".amr";
	}

	@Override
	public void start() {
		if (!isRecording) {
			try {
				recorder.prepare();
				recorder.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			isRecording = true;
		}

	}

	@Override
	public void stop() {
		if (isRecording) {
			try{
				recorder.stop();
				recorder.reset();
				recorder.release();
                recorder=null;
				isRecording = false;
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

	}

	@Override
	public void deleteOldFile() {
		File file = new File(fileFolder + "/SendVoice.amr");
		if (file.exists()) {
			file.delete();
		}
		//file.deleteOnExit();
	}

	@Override
	public double getAmplitude() {
		if (!isRecording) {
			return 0;
		}
        try {
            return recorder.getMaxAmplitude();
        } catch (Exception e) {
            return 0;
        }
    }

}
