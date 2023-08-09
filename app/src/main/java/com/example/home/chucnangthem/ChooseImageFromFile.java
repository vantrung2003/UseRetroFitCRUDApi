package com.example.home.chucnangthem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.home.R;
import com.example.home.api.ApiService;
import com.example.home.api.RetrofitClientTest;
import com.example.home.model.Modeltest;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseImageFromFile extends AppCompatActivity {
    private static final int REQUEST_PICK_IMAGE = 1;
    private Button selectImageButton, uploadImageButton;
    private ApiService apiService;
    private ImageView selecImageView;

    private RetrofitClientTest tttt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image_from_file);
        selectImageButton = findViewById(R.id.selectImageButton);
        selecImageView = findViewById(R.id.selectedImageView);
        uploadImageButton = findViewById(R.id.uploadImageButton);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        apiService = RetrofitClientTest.getRetrofitInstance().create(ApiService.class);

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selecImageView.getDrawable() == null) {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Vui lòng chọn 1 hình ảnh!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Uri imageUri = (Uri) selecImageView.getTag();
                    uploadImage(imageUri);
                }
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    private void uploadImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File imageFile = createImageFileFromStream(inputStream);
            // Convert the image to Base64
            String base64Image = convertImageToBase64(imageFile);
            Log.d("TAG", "uploadImage: " + base64Image);

            String mimeType = getContentResolver().getType(imageUri);
            String fileExtension;

            if (mimeType != null && mimeType.equals("image/png")) {
                fileExtension = "png";
            } else {
                fileExtension = "jpg";
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/" + fileExtension), base64Image);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", base64Image.toString(), requestFile);
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "Image description");

            Call<Modeltest> call = apiService.uploadImage(body, description);
            call.enqueue(new Callback<Modeltest>() {
                @Override
                public void onResponse(Call<Modeltest> call, Response<Modeltest> response) {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Đã thêm mới dữ liệu! Vui lòng get lại data để update!", Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Modeltest> call, Throwable t) {
                    Log.d("TAG", "onFailure: " + t.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertImageToBase64(File imageFile) {
        byte[] bytes;
        try {
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            bytes = new byte[(int) imageFile.length()];
            fileInputStream.read(bytes);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                selecImageView.setTag(selectedImageUri);
                Glide.with(ChooseImageFromFile.this)
                        .load(selectedImageUri)
                        .into(selecImageView);
            }
        }
    }

    private File createImageFileFromStream(InputStream inputStream) throws IOException {
        File file = new File(getCacheDir(), "temp_image.jpg");
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();
        return file;
    }
}
