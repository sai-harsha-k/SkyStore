import React, { useState } from 'react';
import axios from 'axios';
import { Container, Row, Col, Card, Form, Button } from 'react-bootstrap';
import './FileUpload.css'; // Import custom CSS for additional styling

function FileUpload() {
  const [file, setFile] = useState(null);
  const [uploaderId, setUploaderId] = useState('');
  const [description, setDescription] = useState('');
  const [uploadStatus, setUploadStatus] = useState('');

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('uploaderId', uploaderId);
    formData.append('description', description);

    try {
      const response = await axios.post('http://localhost:8080/files/upload', formData);
      setUploadStatus('File uploaded successfully!');
    } catch (error) {
      console.error('Error uploading file: ', error);
      setUploadStatus('Error uploading file.');
    }
  };

  return (
    <Container className="file-upload-container">
      <Row className="justify-content-md-center">
        <Col md={6}>
          <Card className="file-upload-card">
            <Card.Body>
              <Card.Title className="text-center">Upload File</Card.Title>
              <Form>
                <Form.Group controlId="formFile" className="mb-3">
                  <Form.Label>Select File</Form.Label>
                  <Form.Control type="file" onChange={handleFileChange} />
                </Form.Group>
                <Form.Group controlId="formUploaderId" className="mb-3">
                  <Form.Label>Uploader ID</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Enter your ID"
                    value={uploaderId}
                    onChange={(e) => setUploaderId(e.target.value)}
                  />
                </Form.Group>
                <Form.Group controlId="formDescription" className="mb-3">
                  <Form.Label>Description</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Enter file description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                  />
                </Form.Group>
                <Button variant="primary" onClick={handleUpload}>
                  Upload
                </Button>
                {uploadStatus && <div className="upload-status mt-3">{uploadStatus}</div>}
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}

export default FileUpload;
