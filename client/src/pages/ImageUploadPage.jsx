import ImageUploader from "../components/ImageUploader";

const API_BASE = import.meta.env.VITE_API_URL || "";
const UPLOAD_URL = `${API_BASE}/api/images` || "";         // backend expects @RequestParam("file")



export default function ImageUploadPage() {
    return (
        <div className="flex min-h-screen items-center justify-center">
            <div className="w-full max-w-3xl px-4 py-10 text-center">
                <h1 className="mb-1 text-3xl font-bold text-white">Upload an image of mushroom</h1>
                <p className="mb-6 text-slate-400">
                    Drag & drop a photo below, or click to choose a file.
                </p>

                <ImageUploader uploadUrl={UPLOAD_URL} maxSizeMB={15} />
            </div>
        </div>
    );
}
