import { Icon } from '@iconify/react';

const iconSize = 45;

export default function Navbar() {
    return (
        <nav className="p-4">
            <div className="flex w-full justify-between px-10">
                <div className="mb-1 text-2xl font-bold">
                    Mushroom Vision
                </div>
                <div className="flex gap-4">
                    <a
                        href="https://github.com/connorb0531"
                        target="_blank"
                        rel="noopener noreferrer"
                        className="hover:text-gray-400"
                    >
                        <Icon
                            icon="mdi:github"
                            width={iconSize}
                            height={iconSize}
                        />
                    </a>
                    <a
                        href="https://www.linkedin.com/in/connor-buckley-b36772272"
                        target="_blank"
                        rel="noopener noreferrer"
                        className="hover:text-gray-400"
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
