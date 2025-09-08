import { useCallback, useRef, useState } from "react";

export default function ImageUploader({ uploadUrl, maxSizeMB = 15 }) {
    const [file, setFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState("");
    const [uploading, setUploading] = useState(false);
    const [progress, setProgress] = useState(0);
    const [success, setSuccess] = useState("");   // success message
    const [error, setError] = useState("");
    const inputRef = useRef(null);

    const onFilesPicked = useCallback(
        (files) => {
            setError("");
            setSuccess("");
            const f = files?.[0];
            if (!f) return;

            if (!f.type.startsWith("image/")) {
                setError("Please choose an image file.");
                return;
            }
            if (f.size > maxSizeMB * 1024 * 1024) {
                setError(`Image too large (max ${maxSizeMB}MB).`);
                return;
            }

            setFile(f);
            setPreviewUrl(URL.createObjectURL(f));
        },
        [maxSizeMB]
    );

    const onDrop = useCallback(
        (e) => {
            e.preventDefault();
            e.stopPropagation();
            onFilesPicked(e.dataTransfer.files);
        },
        [onFilesPicked]
    );

    const onBrowseClick = () => inputRef.current?.click();

    const clearSelection = () => {
        setFile(null);
        setPreviewUrl("");
        setProgress(0);
        setError("");
        setSuccess("");
        if (inputRef.current) inputRef.current.value = "";
    };

    const upload = async () => {
        if (!file) return;
        if (!uploadUrl) {
            setError("No upload URL configured.");
            return;
        }

        setUploading(true);
        setProgress(0);
        setError("");
        setSuccess("");

        try {
            const form = new FormData();
            form.append("file", file);

            const xhr = new XMLHttpRequest();
            xhr.open("POST", uploadUrl);

            xhr.upload.onprogress = (evt) => {
                if (evt.lengthComputable) {
                    setProgress(Math.round((evt.loaded / evt.total) * 100));
                }
            };

            xhr.onload = () => {
                setUploading(false);
                if (xhr.status >= 200 && xhr.status < 300) {
                    setSuccess("Upload successful.");
                } else {
                    setError(`Upload failed (${xhr.status})`);
                }
            };

            xhr.onerror = () => {
                setUploading(false);
                setError("Network error during upload.");
            };

            xhr.send(form);
        } catch (e) {
            setUploading(false);
            setError(e?.message || "Upload error");
        }
    };

    const dropZoneClass =
        "mx-auto w-full max-w-xl grid place-items-center min-h-72 " +
        "border-2 border-dashed rounded-2xl p-8 text-center cursor-pointer " +
        "bg-white shadow-sm hover:shadow transition";

    return (
        <>
            <div
                className={dropZoneClass}
                onDragOver={(e) => e.preventDefault()}
                onDrop={onDrop}
                // Make the container open the file picker ONLY when no file is selected
                onClick={!file ? onBrowseClick : undefined}
                role={!file ? "button" : undefined}
                tabIndex={!file ? 0 : -1}
            >
                <input
                    ref={inputRef}
                    type="file"
                    accept="image/*"
                    onChange={(e) => onFilesPicked(e.target.files)}
                    className="hidden"
                />

                {!file ? (
                    <div className="space-y-1">
                        <div className="text-lg font-medium">Drop image here</div>
                        <div className="text-sm text-slate-500">or click to browse</div>
                    </div>
                ) : (
                    <div className="flex flex-col items-center gap-4">
                        <img
                            src={previewUrl}
                            alt="preview"
                            className="max-w-full rounded-xl shadow-lg"
                            onLoad={() => URL.revokeObjectURL(previewUrl)}
                        />

                        <div className="text-sm text-slate-500">
                            {file.name} • {(file.size / 1024 / 1024).toFixed(2)} MB
                        </div>

                        <div className="flex gap-3">
                            <button
                                disabled={uploading}
                                onClick={(e) => {
                                    e.stopPropagation(); // prevent container click
                                    upload();
                                }}
                                className="inline-flex items-center rounded-full bg-blue-600 px-4 py-2 font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
                            >
                                {uploading ? "Uploading…" : "Upload"}
                            </button>

                            <button
                                disabled={uploading}
                                onClick={(e) => {
                                    e.stopPropagation(); // prevent container click
                                    clearSelection();
                                }}
                                className="inline-flex items-center rounded-full border border-slate-300 bg-white px-4 py-2 font-semibold text-slate-800 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
                            >
                                Clear
                            </button>
                        </div>

                        {uploading && (
                            <div className="w-full max-w-md">
                                <div className="h-2 w-full overflow-hidden rounded-full bg-slate-200">
                                    <div
                                        className="h-full bg-blue-600 transition-[width]"
                                        style={{ width: `${progress}%` }}
                                    />
                                </div>
                                <div className="mt-2 text-center text-xs text-slate-500">
                                    {progress}%
                                </div>
                            </div>
                        )}
                    </div>
                )}
            </div>

            {success && (
                <div className="mt-4 rounded-xl border border-green-200 bg-green-50 px-4 py-3 text-green-700">
                    {success}
                </div>
            )}

            {error && (
                <div className="mt-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-red-700">
                    {error}
                </div>
            )}
        </>
    );
}
