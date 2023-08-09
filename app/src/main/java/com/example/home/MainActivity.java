package com.example.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.home.api.RetroFitClient;
import com.example.home.api.TaskWork;
import com.example.home.chucnangthem.ChooseImageFromFile;
import com.example.home.model.Taskword_ph19997;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Adapter adapter;
private RecyclerView recyclerView;
private Button btnGet,btnChuyenMan;
private  ImageView imgAdd,btnTimkiem;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rcy);
        btnGet = findViewById(R.id.btnGet);
        imgAdd = findViewById(R.id.btnThemmoi);
        btnTimkiem = findViewById(R.id.btnTimkiem1);
        btnChuyenMan = findViewById(R.id.btnChuyenMan);
        btnChuyenMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChooseImageFromFile.class);
                startActivity(i);
                finish();
            }
        });

        //tim kiem
        btnTimkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = findViewById(android.R.id.content);
                Snackbar.make(rootView, "Chức năng đang được bảo trì!!!", Snackbar.LENGTH_SHORT).show();
            }
        });

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo và hiển thị dialog
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.themmoi);
                // Chỉnh kích thước của dialog
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                int width = getResources().getDisplayMetrics().widthPixels; // Kích thước màn hình
                layoutParams.width = (int) (width * 0.9); // Chiều rộng dialog chiếm 90% màn hình
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Chiều cao tự động

                dialog.getWindow().setAttributes(layoutParams);
                // Thêm mã để khởi tạo các EditText và Button trong dialog
                EditText editTitle = dialog.findViewById(R.id.editTitle);
                EditText editContent = dialog.findViewById(R.id.editContent);
                EditText editStatus = dialog.findViewById(R.id.editStatus);
                EditText editImage = dialog.findViewById(R.id.editImage);
                Button btnSave = dialog.findViewById(R.id.btnSave);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Lấy dữ liệu từ các EditText
                        String title = editTitle.getText().toString();
                        String content = editContent.getText().toString();
                        String image = editImage.getText().toString();

                        if (title.isEmpty() || content.isEmpty() || image.isEmpty()) {
                            // Hiển thị thông báo sử dụng Snackbar
                            View rootView = findViewById(android.R.id.content);
                            Snackbar.make(rootView, "Vui lòng nhập đầy đủ thông tin", Snackbar.LENGTH_SHORT).show();
                        } else {
                            // Lấy ngày hiện tại
                            Date currentDate = new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String formattedDate = dateFormat.format(currentDate);
                            // Tạo đối tượng Taskword_ph19997 để gửi lên máy chủ
                            Taskword_ph19997 task = new Taskword_ph19997();
                            task.setTitle_ph19997(title);
                            task.setContent_ph19997(content);
                            task.setEnd_date_ph19997(formattedDate);
                            task.setStatus_ph19997(new Random().nextInt(2)); // Tạo ngẫu nhiên giá trị 0 hoặc 1
                            task.setImage_ph19997(image);

                            // Gửi dữ liệu lên máy chủ
                            TaskWork taskWork = RetroFitClient.getRetrofitInstance().create(TaskWork.class);
                            Call<Taskword_ph19997> call = taskWork.postDataToServer(task);

                            call.enqueue(new Callback<Taskword_ph19997>() {
                                @Override
                                public void onResponse(Call<Taskword_ph19997> call, Response<Taskword_ph19997> response) {
                                    View rootView = findViewById(android.R.id.content);
                                    Snackbar.make(rootView, "Đã thêm mới dữ liệu! Vui lòng get lại data để update!", Snackbar.LENGTH_SHORT).show();
                                    // Đóng dialog sau khi thêm mới
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<Taskword_ph19997> call, Throwable t) {
                                    Log.d("TAG", "onFailure: " + t.getMessage());
                                }
                            });
                        }
                    }
                });

                // Hiển thị dialog
                dialog.show();
            }
        });

//get danh sách bản ghi
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskWork taskWork = RetroFitClient.getRetrofitInstance().create(TaskWork.class);
                Call<List<Taskword_ph19997>> call = taskWork.getAllData();

                call.enqueue(new Callback<List<Taskword_ph19997>>() {
                    @Override
                    public void onResponse(Call<List<Taskword_ph19997>> call, Response<List<Taskword_ph19997>> response) {
                       if (response.isSuccessful()){
                           loadDataList(response.body());
                           updateRecyclerView(response.body());
                           Toast.makeText(getApplicationContext(), "Đang lấy dữ liệu vui lòng đợi!!!", Toast.LENGTH_SHORT).show();
                       }
                    }

                    @Override
                    public void onFailure(Call<List<Taskword_ph19997>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Unable to load users", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "onFailure: "+t.getMessage());
                    }
                });
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //load list data
    private void loadDataList(List<Taskword_ph19997> body) {
        adapter = new Adapter(body, this, new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Taskword_ph19997 task) {
                showDetailDialog(task);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Cập nhật tổng số lượng bản ghi
        TextView totalRecordsTextView = findViewById(R.id.totalRecordsTextView);
        totalRecordsTextView.setText("Tổng số bản ghi: " + body.size());
    }

//show thong tin chi tiet
    private void showDetailDialog(Taskword_ph19997 task) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.chitiettask);

        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dialogContent = dialog.findViewById(R.id.dialogContent);
        TextView dialogEndDate = dialog.findViewById(R.id.dialogEndDate);
        TextView dialogStatus = dialog.findViewById(R.id.dialogStatus);
        ImageView img = dialog.findViewById(R.id.imgView2);

        dialogTitle.setText("Titile: " + task.getTitle_ph19997());
        dialogContent.setText("Content: " + task.getContent_ph19997());
        dialogEndDate.setText("EndDate: " + task.getEnd_date_ph19997());
        dialogStatus.setText("Status: " + task.getStatus_ph19997());
        Glide.with(this)
                .load(task.getImage_ph19997())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.error)
                .into(img);

        Button btnDel = dialog.findViewById(R.id.btnXoa);
        Button btnSua = dialog.findViewById(R.id.btnSua);

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Xóa bản ghi");
                builder.setMessage("Bạn có chắc chắn muốn xóa bản ghi này?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Gọi phương thức xóa bản ghi ở đây
                        deleteTask(task);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
//btn Sua
        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo và hiển thị dialog sửa
                Dialog dialog1 = new Dialog(MainActivity.this);
                dialog1.setContentView(R.layout.sua);

                EditText editTitle = dialog1.findViewById(R.id.editTitle);
                EditText editContent = dialog1.findViewById(R.id.editContent);
                EditText editEndDate = dialog1.findViewById(R.id.editEndDate);
                EditText editImage = dialog1.findViewById(R.id.editImage);
                EditText editStatus = dialog1.findViewById(R.id.editStatus);
                Button btnSave = dialog1.findViewById(R.id.btnSave);

                editStatus.setText(String.valueOf(task.getStatus_ph19997()));
                editTitle.setText(task.getTitle_ph19997());
                editContent.setText(task.getContent_ph19997());
                editEndDate.setText(task.getEnd_date_ph19997());
                editImage.setText(task.getImage_ph19997());

                // Chỉnh kích thước của dialog
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog1.getWindow().getAttributes());
                int width = getResources().getDisplayMetrics().widthPixels; // Kích thước màn hình
                layoutParams.width = (int) (width * 0.9); // Chiều rộng dialog chiếm 90% màn hình
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Chiều cao tự động
                dialog1.getWindow().setAttributes(layoutParams);

                dialog1.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Lấy dữ liệu từ các EditText trong dialog sửa
                        String title = editTitle.getText().toString();
                        String content = editContent.getText().toString();
                        String endDate = editEndDate.getText().toString();
                        String image = editImage.getText().toString();
                        int status = Integer.parseInt(editStatus.getText().toString());

                        // Cập nhật dữ liệu của đối tượng task
                        task.setTitle_ph19997(title);
                        task.setContent_ph19997(content);
                        task.setEnd_date_ph19997(endDate);
                        task.setStatus_ph19997(status);
                        task.setImage_ph19997(image);

                        // Gửi dữ liệu lên máy chủ để cập nhật
                        TaskWork taskWork = RetroFitClient.getRetrofitInstance().create(TaskWork.class);
                        Call<Taskword_ph19997> call = taskWork.fixDataOfServer(task.getId(),task);

                        call.enqueue(new Callback<Taskword_ph19997>() {
                            @Override
                            public void onResponse(Call<Taskword_ph19997> call, Response<Taskword_ph19997> response) {
                                View rootView = findViewById(android.R.id.content);
                                Snackbar.make(rootView, "Đã sửa thành công bản ghi này", Snackbar.LENGTH_SHORT).show();
                                dialog1.dismiss();

                                // Refresh lại danh sách sau khi đã cập nhật thành công
                                taskWork.getAllData();
                            }

                            @Override
                            public void onFailure(Call<Taskword_ph19997> call, Throwable t) {
                                Log.d("TAG", "onFailure: " + t.getMessage());
                            }
                        });
                    }
                });
            }
        });
        dialog.show();
    }
    //delete 1 bản ghi
    private void deleteTask(Taskword_ph19997 task) {
        TaskWork taskWork = RetroFitClient.getRetrofitInstance().create(TaskWork.class);
        Call<List<Taskword_ph19997>> call = taskWork.deleteDataToServer(task.getId());

        call.enqueue(new Callback<List<Taskword_ph19997>>() {
            @Override
            public void onResponse(Call<List<Taskword_ph19997>> call, Response<List<Taskword_ph19997>> response) {
                if (response.isSuccessful()) {
                    // Xóa bản ghi thành công, cập nhật RecyclerView
                    View rootView = findViewById(android.R.id.content);
                    Snackbar.make(rootView, "Đã xóa bản ghi này", Snackbar.LENGTH_SHORT).show();
                    taskWork.getAllData();
                } else {
                    Log.d("TAG", "Delete request failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Taskword_ph19997>> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

//cap nhat du lieu
    private void updateRecyclerView(List<Taskword_ph19997> newData) {
        adapter.getItemCount();
        adapter.notifyDataSetChanged();
    }
}