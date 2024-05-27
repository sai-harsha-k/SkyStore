import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Tooltip } from 'react-tooltip';
import Modal from 'react-modal';
import { Card, Button, Container, Row, Col } from 'react-bootstrap';
import './FileList.css'; // Import custom CSS for additional styling

Modal.setAppElement('#root'); // Bind modal to your app element

function FileList() {
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [modalIsOpen, setModalIsOpen] = useState(false);
  const [fileUrl, setFileUrl] = useState('');

  useEffect(() => {
    fetchFiles();
  }, []);

  const fetchFiles = async () => {
    try {
      const response = await axios.get('http://localhost:8080/files/metadata');
      console.log('Fetched files:', response.data); // Log to verify data structure
      setFiles(response.data);
    } catch (error) {
      console.error('Error fetching files: ', error);
    }
  };

  const formatUploadDate = (timestamp) => {
    const date = new Date(parseInt(timestamp, 10));
    return date.toLocaleString();
  };

  const handleFileClick = async (file) => {
    setSelectedFile(file);
    setModalIsOpen(true);

    try {
      const response = await axios.get('http://localhost:8080/files/presigned-url', {
        params: {
          fileName: file.fileName
        }
      });
      setFileUrl(response.data);
    } catch (error) {
      console.error('Error fetching presigned URL: ', error);
    }
  };

  const closeModal = () => {
    setModalIsOpen(false);
    setSelectedFile(null);
    setFileUrl('');
  };

  return (
    <div className="file-list-container">
      <Container>
        <h2 className="text-center text-light my-4">File List</h2>
        <Row>
          {files.map((file) => (
            <Col md={4} key={file.fileId} className="mb-4">
              <Card className="file-card" onClick={() => handleFileClick(file)} data-tooltip-id={file.fileId}>
                <Card.Body>
                  <Card.Title className="file-title">{file.fileName}</Card.Title>
                  <Card.Text className="file-description">
                    {file.description}
                  </Card.Text>
                </Card.Body>
                <Tooltip id={file.fileId} place="top" effect="solid">
                  <div>
                    <p><strong>Description:</strong> {file.description}</p>
                    <p><strong>Uploader ID:</strong> {file.uploaderId}</p>
                    <p><strong>Upload Date:</strong> {formatUploadDate(file.uploadDate)}</p>
                    <p><strong>File Size:</strong> {file.fileSize} bytes</p>
                    <p><strong>File Type:</strong> {file.fileType}</p>
                    <p><strong>New Attribute 1:</strong> {file.newAttribute1}</p>
                    <p><strong>New Attribute 2:</strong> {file.newAttribute2}</p>
                  </div>
                </Tooltip>
              </Card>
            </Col>
          ))}
        </Row>
      </Container>

      <Modal
        isOpen={modalIsOpen}
        onRequestClose={closeModal}
        contentLabel="File Content"
        className="file-modal"
        overlayClassName="file-modal-overlay"
      >
        {selectedFile && (
          <div>
            <h2>{selectedFile.fileName}</h2>
            <p><strong>Description:</strong> {selectedFile.description}</p>
            <p><strong>Uploader ID:</strong> {selectedFile.uploaderId}</p>
            <p><strong>Upload Date:</strong> {formatUploadDate(selectedFile.uploadDate)}</p>
            <p><strong>File Size:</strong> {selectedFile.fileSize} bytes</p>
            <p><strong>File Type:</strong> {selectedFile.fileType}</p>
            <p><strong>New Attribute 1:</strong> {selectedFile.newAttribute1}</p>
            <p><strong>New Attribute 2:</strong> {selectedFile.newAttribute2}</p>
            {fileUrl && (
              <iframe src={fileUrl} width="100%" height="600px" title="File Content"></iframe>
            )}
            <Button variant="secondary" onClick={closeModal}>Close</Button>
          </div>
        )}
      </Modal>
    </div>
  );
}

export default FileList;
