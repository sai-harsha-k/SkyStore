### README.md

```markdown
# Cloud File Management Application

This is a full-stack cloud file management application built with Spring Boot and React. The application allows users to upload files to AWS S3, view metadata stored in DynamoDB, and display the contents of files using pre-signed URLs.

## Features

- **File Upload**: Upload files to AWS S3 with metadata stored in DynamoDB.
- **File List**: View a list of uploaded files with detailed metadata.
- **File Preview**: View the contents of files using pre-signed URLs.
- **Dark Theme**: A visually appealing dark theme with modern design.

## Technologies Used

### Backend

- Java
- Spring Boot
- AWS SDK for Java (S3, DynamoDB, Lambda)
- Maven

### Frontend

- React
- React Bootstrap
- Axios
- React Tooltip
- React Modal

## Prerequisites

- Java 8 or higher
- Node.js and npm
- AWS account with access to S3, DynamoDB, and Lambda
- AWS CLI configured with your credentials

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/yourusername/cloud-file-management-app.git
cd cloud-file-management-app
```

### Backend Setup

1. **Navigate to the backend directory**:
    ```bash
    cd backend
    ```

2. **Configure AWS Credentials**:
   - Update `application.properties` with your AWS credentials and bucket name.
   - File: `src/main/resources/application.properties`
     ```properties
     aws.accessKeyId=YOUR_ACCESS_KEY_ID
     aws.secretKey=YOUR_SECRET_KEY
     aws.region=YOUR_AWS_REGION
     ```

3. **Build and Run the Backend**:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

### Frontend Setup

1. **Navigate to the frontend directory**:
    ```bash
    cd frontend
    ```

2. **Install Dependencies**:
    ```bash
    npm install
    ```

3. **Start the Frontend**:
    ```bash
    npm start
    ```

### AWS Lambda Setup

1. **Create a Lambda Function**:
   - Go to the AWS Lambda console and create a new Lambda function named `EnhancedProcessFileLambda`.

2. **Set up the Lambda function**:
   - Use the following Python code for your Lambda function:

```python
import json

def lambda_handler(event, context):
    file_id = event.get('fileId')
    bucket_name = event.get('bucketName')
    file_name = event.get('fileName')
    file_size = event.get('fileSize')
    file_type = event.get('fileType')
    uploader_id = event.get('uploaderId')
    description = event.get('description')

    # Perform a simple transformation: Convert file name to uppercase
    transformed_file_name = file_name.upper()
    
    # Log file metadata and transformations
    print(f"Processing file metadata: fileId={file_id}, fileName={file_name}, fileSize={file_size}, fileType={file_type}, uploaderId={uploader_id}, description={description}")
    print(f"Transformed file name: {transformed_file_name}")
    
    # Return a response with the transformed data
    response = {
        'fileId': file_id,
        'originalFileName': file_name,
        'transformedFileName': transformed_file_name,
        'fileSize': file_size,
        'fileType': file_type,
        'uploaderId': uploader_id,
        'description': description
    }
    
    return {
        'statusCode': 200,
        'body': json.dumps(response)
    }
```

3. **Set up IAM Role**:
   - Ensure your Lambda function has an IAM role with permissions to access S3 and CloudWatch Logs.

4. **Update the Spring Boot Application**:
   - The Spring Boot application should be configured to invoke this Lambda function.

### Access the Application

Open your browser and go to `http://localhost:3000` to access the application.

## File Structure

```
cloud-file-management-app/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/cloudapp/cloud_application/
│   │   │   │       ├── config/                # AWS configuration
│   │   │   │       ├── controller/            # REST controllers
│   │   │   │       ├── service/               # Services for S3, DynamoDB, Lambda
│   │   │   │       └── CloudApplication.java  # Main application class
│   │   │   ├── resources/
│   │   │   │   ├── application.properties     # Application properties
│   └── pom.xml                                # Maven dependencies
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── FileUpload.js                  # File upload component
│   │   │   ├── FileList.js                    # File list component
│   │   ├── App.js                             # Main app component
│   │   ├── App.css                            # Main app styles
│   │   ├── index.js                           # React entry point
│   ├── public/
│   ├── package.json                           # npm dependencies
└── README.md                                  # Project documentation
```

## Usage

- **Upload File**: Select a file, enter your ID, and a description, then click the upload button.
- **View Files**: Click on a file card to view its metadata and contents.
```

### Instructions

1. **Replace placeholders** such as `YOUR_ACCESS_KEY_ID`, `YOUR_SECRET_KEY`, `YOUR_AWS_REGION`, and `yourusername` with your actual information.


This updated `README.md` file now includes the setup instructions for AWS Lambda and should provide clear and comprehensive documentation for your project, making it easy for others to understand and contribute.
