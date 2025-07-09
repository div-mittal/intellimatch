import Image from "next/image";

export default function Home() {
  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <h1 className="text-4xl font-bold">Welcome to IntelliMatch</h1>
      <p className="text-lg">Your AI-powered resume matcher and rewriter</p>

      <a
        href="/upload"
        className="mt-8 px-6 py-3 bg-blue-600 text-white rounded-lg shadow hover:bg-blue-700 transition-colors font-semibold"
      >
        Go to Upload Page
      </a>
    </div>
  );
}
