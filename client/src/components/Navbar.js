import { Icon } from '@iconify/react';

const iconSize = 45;

export default function Navbar() {
    return (
        <nav className="p-4 bg-white/50 backdrop-blur-sm border-b border-gray-200">
            <div className="flex w-full justify-between px-10">
                <div className="mb-1 text-2xl font-bold text-gray-800">
                    Mushroom Vision
                </div>
                <div className="flex gap-4">
                    <a
                        href="https://github.com/connorb0531"
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-gray-600 hover:text-gray-800 transition-colors"
                    >
                        <Icon
                            icon="mdi:github"
                            width={iconSize}
                            height={iconSize}
                        />
                    </a>
                    <a
                        href="https://www.linkedin.com/in/connorabuckley/"
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-gray-600 hover:text-gray-800 transition-colors"
                    >
                        <Icon
                            icon="mdi:linkedin"
                            width={iconSize}
                            height={iconSize}
                        />
                    </a>
                </div>
            </div>
        </nav>
    );
}
