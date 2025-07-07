import { useEffect, useState } from "react";

export const DateTimeDisplay = () => {
  const [dateTime, setDateTime] = useState("");

  useEffect(() => {
    const now = new Date();
    const options: Intl.DateTimeFormatOptions = {
      weekday: "long",
      month: "short",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    };
    const formatted = now.toLocaleString("en-US", options);
    setDateTime(formatted.replace(",", ""));
  }, []);

  return <div>{dateTime}</div>;
};
