# IntelliMatch Frontend

## Project Description

IntelliMatch is an AI-powered resume and job description matching application that provides ATS (Applicant Tracking System) optimization analysis. This is the frontend application built with React, TypeScript, and Vite.

## Tech Stack

- **React 18** - UI Framework
- **TypeScript** - Type Safety
- **Vite** - Build Tool
- **React Router** - Navigation
- **TailwindCSS** - Styling
- **shadcn/ui** - UI Components
- **Tanstack Query** - Data Fetching
- **Sonner** - Toast Notifications

## Getting Started

### Prerequisites

- Node.js 18+ and npm installed - [install with nvm](https://github.com/nvm-sh/nvm#installing-and-updating)
- Backend server running on `http://localhost:8090`

### Installation

```sh
# Step 1: Clone the repository
git clone <YOUR_GIT_URL>

# Step 2: Navigate to the frontend directory
cd intellimatch-frontend

# Step 3: Install dependencies
npm install

# Step 4: Start the development server
npm run dev
```

The application will be available at `http://localhost:8080` (or `8081` if 8080 is in use).

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## Project Structure

```
src/
├── components/     # Reusable UI components
├── hooks/          # Custom React hooks
├── lib/            # Utility functions and API clients
├── pages/          # Page components
└── types/          # TypeScript type definitions
```

## Features

- User Authentication (Login/Register)
- Resume & Job Description Upload
- AI-Powered Match Analysis
- Match History Dashboard
- Detailed Match Results with ATS Scoring
- Protected Routes
- Responsive Design

## Backend Integration

The frontend communicates with the backend API running at `http://localhost:8090`. API requests are proxied through Vite's dev server configuration.

## Contributing

1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## License

Private Project
- Edit files directly within the Codespace and commit and push your changes once you're done.

## What technologies are used for this project?

This project is built with:

- Vite
- TypeScript
- React
- shadcn-ui
- Tailwind CSS

## How can I deploy this project?

Simply open [Lovable](https://lovable.dev/projects/050b80f1-10ed-499a-8aaf-a64828dacbb4) and click on Share -> Publish.

## Can I connect a custom domain to my Lovable project?

Yes, you can!

To connect a domain, navigate to Project > Settings > Domains and click Connect Domain.

Read more here: [Setting up a custom domain](https://docs.lovable.dev/features/custom-domain#custom-domain)
