import { useEffect } from "react";
import { useMap } from "react-leaflet";

function ClosePopupsOnDayChange({ selectedDayNumber }) {
  const map = useMap();

  useEffect(() => {
    map.closePopup();
  }, [selectedDayNumber, map]);

  return null;
}

export default ClosePopupsOnDayChange;
