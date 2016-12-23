package inputcell;

import java.io.ByteArrayOutputStream;

import com.swqs.schooltrade.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageInputCellFragment extends Fragment {
	final int REQUESTCODE_CAMERA = 1;
	final int REQUESTCODE_ALBUM = 2;
	
	ImageView imageView;
	TextView hintText;
	
	byte[] pngData;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inputcell_image, container);
		
		imageView = (ImageView) view.findViewById(R.id.image);
		hintText = (TextView) view.findViewById(R.id.hint);
		
		imageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onImageViewClicked();
			}
		});
		
		return view;
	}
	
	void onImageViewClicked(){
		String[] items = {
				"≈ƒ’’",
				"œ‡≤·"
		};

		new AlertDialog.Builder(getActivity())
		.setTitle(hintText.getText())
		.setItems(items, new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
				case 0:
					takePhoto();
					break;

				case 1:
					pickFromAlbum();
					break;

				default:
					break;
				}
			}
		})

		.show();
	}

	void takePhoto(){
		Intent itnt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(itnt, REQUESTCODE_CAMERA);
	}

	void pickFromAlbum(){
		Intent itnt = new Intent(Intent.ACTION_GET_CONTENT);
		itnt.setType("image/*");
		startActivityForResult(itnt, REQUESTCODE_ALBUM);
	}

	void saveBitmap(Bitmap bmp){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, baos);
		pngData = baos.toByteArray();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Activity.RESULT_CANCELED) return;
		if(requestCode == REQUESTCODE_CAMERA){
			Bitmap bmp = (Bitmap)data.getExtras().get("data");
			saveBitmap(bmp);
			imageView.setImageBitmap(bmp);			
		}else if(requestCode == REQUESTCODE_ALBUM){
			try{
				Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
				saveBitmap(bmp);
				imageView.setImageBitmap(bmp);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public void setHintText(String hintText){
		this.hintText.setText(hintText);
	}	

	public byte[] getPngData(){
		return pngData;
	}
}

