import React from 'react';
import FileUpload from './FileUpload';
import FileList from './FileList';
import './App.css'; // Custom CSS for overall styling

function App() {
  return (
    <div className="App">
      <FileUpload />
      <FileList />
    </div>
  );
}

export default App;
