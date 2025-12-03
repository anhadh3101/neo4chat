import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Explore from './pages/Explore';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/explore" element={<Explore />} />
        <Route path="/" element={
          <div className="container mx-auto p-6">
            <h1 className="text-3xl font-bold mb-4">Neo4Chat</h1>
            <p className="mb-4">Welcome to Neo4Chat!</p>
            <a href="/explore" className="text-primary hover:underline">
              Go to Explore Page
            </a>
          </div>
        } />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
