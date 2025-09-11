import ImageUploadPage from "./pages/ImageUploadPage.jsx";
import Navbar from "./components/Navbar.jsx";

export default function App() {
    return (
        <div className="h-screen w-screen overflow-hidden flex flex-col">
            <Navbar />
            <ImageUploadPage />
        </div>
    );
}
