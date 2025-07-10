import { useState, useEffect, useRef } from "react";

const TIMER = 30;

const useTimer = (initialValue: number = TIMER) => {
  const [timer, setTimer] = useState(initialValue);

  // useEffect(() => {
  //   const interval = setInterval(() => {
  //     setTimer((prev) => prev - 1);
  //   }, 1000);

  //   return () => clearInterval(interval);
  // }, []);

  const intervalRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    // If timer is already running or timer <= 0, do nothing
    if (timer <= 0 || intervalRef.current !== null) return;

    intervalRef.current = setInterval(() => {
      setTimer((prev) => {
        if (prev <= 1) {
          clearInterval(intervalRef.current!);
          intervalRef.current = null;
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => {
      clearInterval(intervalRef.current!);
      intervalRef.current = null;
    };
  }, [timer]);

  return { timer, setTimer };
};

export default useTimer;
