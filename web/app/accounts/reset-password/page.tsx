export default function ResetPasswordPage() {
  return (
    <div className="bg-green-950 relative w-screen h-screen">
      <div className="bg-green-900 relative z-10 w-screen h-screen overflow-hidden overflow-y-auto flex flex-col">
        <div className="bg-red-500 container min-w-full px-10 lg:px-20 xl:px-36 flex-shrink-0 relative flex items-center justify-between pb-4 transition-all">
          <div className="flex items-center gap-x-2 py-10">logo</div>
          <div className="flex flex-col items-end sm:items-center sm:gap-2 sm:flex-row text-center text-sm font-medium">
            <p>
              New to Sync<span className="text-green-400">Turtle</span>?
            </p>
            create account
          </div>
        </div>
        <div className="bg-blue-500">
          <div className="">
            <div className="">
              <h3 className="">title</h3>
              <p className="">description</p>
            </div>
            form
          </div>
        </div>
      </div>
    </div>
  );
}
